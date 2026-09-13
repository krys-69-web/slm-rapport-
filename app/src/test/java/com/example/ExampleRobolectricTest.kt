package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.generator.FiliereContentResolver
import com.example.generator.ReportGenerator28Pages
import com.example.model.CompanyConfig
import com.example.model.LocationItem
import com.example.model.StudentConfig
import com.example.data.AppDatabase
import com.example.data.DraftedSectionEntity
import com.example.data.DraftedSectionRepository
import com.example.service.DraftedSectionResult
import com.example.service.ReportSectionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SLM Rapport", appName)
  }

  @Test
  fun `verify exactly 28 pages generated`() {
    val student = StudentConfig(
      studentName = "KOUAME KOFFI ARNAUD",
      filiereOption = "Génie Mécanique & Productique",
      schoolName = "INP-HB Yamoussoukro",
      theme = "CONCEPTION ET RÉALISATION D'UNE BARRIÈRE LEVANTE DE SÉCURITÉ INDUSTRIELLE",
      academicTutor = "Dr. KOUASSI N'GUESSAN",
      internshipTutor = "M. BROU PRI"
    )
    val company = CompanyConfig(
      inputName = "ICC CORPORATE",
      rccmBank = "RCCM CI-ABJ-2015-B-29389 / CC 1558991 U"
    )
    val locations = listOf(
      LocationItem("Port Autonome d'Abidjan", "4", "Soudure et pose de platines"),
      LocationItem("Site Industriel Yopougon", "2", "Assemblage du pivot")
    )

    val pages = ReportGenerator28Pages.build28Pages(
      company = company,
      student = student,
      locations = locations,
      finalSummary = "Rapport technique complet",
      illustrations = emptyList()
    )

    assertEquals("Doit générer exactement 28 pages", 28, pages.size)
    assertEquals("Page 1 doit être la page de garde", 1, pages.first().pageNumber)
    assertEquals("Page 28 doit être le procès-verbal", 28, pages.last().pageNumber)
  }

  @Test
  fun `verify compliance audit score and jury simulator questions`() {
    val student = StudentConfig(
      studentName = "KOUAME KOFFI ARNAUD",
      filiereOption = "Électrotechnique & Énergies Renouvelables",
      schoolName = "ESATIC Abidjan",
      theme = "OPTIMISATION ET AUTOMATISATION D'UN POSTE DE TRANSFORMATION MT/BT",
      academicTutor = "Dr. KOUASSI",
      internshipTutor = "M. KOUAME DG"
    )
    val company = CompanyConfig(
      inputName = "CIE ENERGIE",
      rccmBank = "RCCM CI-ABJ-2018-B-12345 / CC 9988776 A"
    )

    val pages = ReportGenerator28Pages.build28Pages(
      company = company,
      student = student,
      locations = emptyList(),
      finalSummary = "Etude électrotechnique",
      illustrations = emptyList()
    )

    val audit = FiliereContentResolver.performComplianceAudit(student, company, pages, "Notes brutes de stage")
    assertTrue("Le score d'audit doit être >= 90", audit.score >= 90)
    assertTrue("Le statut doit être conforme", audit.academicStatus.contains("CONFORME"))

    val juryQuestions = FiliereContentResolver.generateJuryQuestions(
      type = FiliereContentResolver.FiliereType.ELECTROTECHNIQUE_ENERGIE,
      theme = student.theme,
      companyName = company.inputName
    )
    assertTrue("Doit générer au moins 5 questions de jury", juryQuestions.size >= 5)
    assertNotNull(juryQuestions[0].suggestedAnswer)
    assertNotNull(juryQuestions[0].juryExpectation)
  }

  @Test
  fun `verify Room drafted section entity and repository offline persistence`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val database = AppDatabase.getInstance(context)
    val dao = database.draftedSectionDao()
    val repository = DraftedSectionRepository(dao)

    val draftResult = DraftedSectionResult(
      sectionType = ReportSectionType.CADRE_METHODOLOGIQUE_CDCF,
      title = "CADRE MÉTHODOLOGIQUE & CAHIER DES CHARGES",
      draftedContent = "Le présent projet s'articule autour d'une démarche d'ingénierie rigoureuse basée sur la méthode APTE et le cahier des charges fonctionnel.",
      keyPointsHighlighted = listOf("Méthode APTE", "Diagramme Bête à cornes", "Diagramme Pieuvre"),
      isFromGoogleAiSdk = true,
      modelUsed = "gemini-3.5-flash",
      generatedAtMillis = System.currentTimeMillis()
    )

    repository.saveSection(
      result = draftResult,
      studentName = "KOUAME KOFFI ARNAUD",
      companyName = "ICC CORPORATE",
      theme = "CONCEPTION D'UNE BARRIÈRE LEVANTE"
    )

    val fetched = repository.getSectionOnce(ReportSectionType.CADRE_METHODOLOGIQUE_CDCF)
    assertNotNull("La section doit être retrouvée en base locale Room", fetched)
    assertEquals(ReportSectionType.CADRE_METHODOLOGIQUE_CDCF, fetched?.sectionType)
    assertEquals(true, fetched?.isFromGoogleAiSdk)
    assertEquals("gemini-3.5-flash", fetched?.modelUsed)
    assertTrue(fetched?.draftedContent?.contains("APTE") == true)
    assertEquals(3, fetched?.keyPointsHighlighted?.size)

    val count = repository.draftedSectionsCount.first()
    assertTrue("Le nombre de sections en base doit être >= 1", count >= 1)
  }
}

