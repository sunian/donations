import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal

class WordDocFactory(
    private val name: String,
    private val fein: String,
    private val year: Int,
    private val filename: String,
) : DocumentFactory {
    private val document = XWPFDocument()
    private val defaultFont = Defaults.fontFamily

    override fun addFooter() {
        document.createHeaderFooterPolicy()
            .createFooter(XWPFHeaderFooterPolicy.DEFAULT)
            .createParagraph()
            .createRun().apply {
                isBold = true
                fontSize = Defaults.fontSize
                fontFamily = defaultFont
                setText(Defaults.footerText)
            }
    }

    override fun addReport(donations: List<Donation>) {
        document.createParagraph().apply {
            alignment = ParagraphAlignment.CENTER
            spacingBetween = 1.5
            createRun().apply {
                isBold = true
                fontSize = 15
                fontFamily = defaultFont
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
    }

    override fun addPageBreak() {
        document.createParagraph().isPageBreak = true
    }

    override fun writeToFile(): String {
        File(Defaults.outputDir).mkdirs()
        val path = "${Defaults.outputDir}/$filename.docx"
        val out = FileOutputStream(File(path))
        document.write(out)
        out.close()
        println("docx written successfully")
        return path
    }

    private fun XWPFTableCell.setHeaderText(text: String) {
        removeParagraph(0)
        addParagraph().apply {
            alignment = ParagraphAlignment.CENTER
            createRun().apply {
                isBold = true
                fontSize = Defaults.fontSize
                fontFamily = defaultFont
                setText(text)
            }
        }
    }

    private fun XWPFTableCell.setBodyText(text: String, alignment: ParagraphAlignment) {
        removeParagraph(0)
        addParagraph().apply {
            this.alignment = alignment
            createRun().apply {
                fontSize = Defaults.fontSize
                fontFamily = defaultFont
                setText(text)
            }
        }
    }
}