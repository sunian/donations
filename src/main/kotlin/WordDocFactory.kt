import org.apache.poi.ss.usermodel.Color
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy
import org.apache.poi.xwpf.usermodel.*
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder
import java.io.File

import java.io.FileOutputStream
import java.math.BigDecimal


class WordDocFactory(
    private val name: String = "Grace Faith Chinese Church",
    private val fein: String = "82-4202503",
    private val year: Int
) {
    private val document = XWPFDocument()

    fun addFooter() {
        document.createHeaderFooterPolicy()
            .createFooter(XWPFHeaderFooterPolicy.DEFAULT)
            .createParagraph()
            .createRun().apply {
                isBold = true
                fontSize = 11
                setText("No goods or services provided in exchange by the Church.")
            }
    }

    fun addReport(donations: List<Donation>) {
        document.createParagraph().apply {
            alignment = ParagraphAlignment.CENTER
            spacingBetween = 1.5
            createRun().apply {
                isBold = true
                fontSize = 15
                setText(name)
                addCarriageReturn()
                setText("January through December $year Contribution Acknowledgement")
                addCarriageReturn()
                setText("FEIN $fein")
                addBreak()
            }
        }
        document.createTable().apply {
            removeRow(0)
            setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "000000")
            setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "000000")
            createRow().apply {
                removeCell(0)
                addNewTableCell().setHeaderText("Date")
                addNewTableCell().setHeaderText("Name")
                addNewTableCell().setHeaderText("Credit")
            }
            var total = BigDecimal.ZERO
            donations.forEach { donation ->
                total = total.add(donation.getAmount())
                createRow().apply {
                    getCell(0).setBodyText(donation.date, ParagraphAlignment.RIGHT)
                    getCell(1).setBodyText(donation.name, ParagraphAlignment.LEFT)
                    getCell(2).setBodyText(donation.credit, ParagraphAlignment.RIGHT)
                }
            }
            createRow().apply {
                getCell(0).setHeaderText("Total")
                getCell(2).setBodyText(Donation.CURRENCY_FORMAT.format(total), ParagraphAlignment.RIGHT)
            }
        }
        document.createParagraph().isPageBreak = true
    }

    fun writeToFile() {
        val out = FileOutputStream(File("doc.docx"))
        document.write(out)
        out.close()
        println("docx written successfully")
    }

    private fun XWPFTableCell.setHeaderText(text: String) {
        removeParagraph(0)
        addParagraph().apply {
            alignment = ParagraphAlignment.CENTER
            createRun().apply {
                isBold = true
                fontSize = 11
                setText(text)
            }
        }
    }

    private fun XWPFTableCell.setBodyText(text: String, alignment: ParagraphAlignment) {
        removeParagraph(0)
        addParagraph().apply {
            this.alignment = alignment
            createRun().apply {
                fontSize = 11
                setText(text)
            }
        }
    }
}