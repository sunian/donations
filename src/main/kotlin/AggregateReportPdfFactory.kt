import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class AggregateReportPdfFactory(
    private val name: String,
    private val year: Int,
    filename: String,
) : DocumentFactory {
    private val document = Document(PageSize.A3, 0f, 0f, 4f, 4f)
    private val table = PdfPTable(13)
    private val defaultFont = Defaults.fontFamily
    private val checkmark = Image.getInstance(URL("https://www.iconsdb.com/icons/download/green/checkmark-256.png"))
    private val outputPath = "${Defaults.outputDir}/$year/$filename.pdf"
    private val pdfWriter: PdfWriter
    private var rowBG: Boolean = true

    init {
        File(outputPath).parentFile.mkdirs()
        pdfWriter = PdfWriter.getInstance(document, FileOutputStream(outputPath))
        document.open()
        FontFactory.getFont(defaultFont, Defaults.fontSizeF + 3, Font.BOLD, BaseColor.BLACK).let { font ->
            val p = Paragraph()
            p.alignment = Element.ALIGN_CENTER
            p.add(Chunk("$name $year Contributions by Month\n", font))
            document.add(p)
        }
        document.add(Paragraph("\n"))
        table.setWidths(IntArray(13) { if (it == 0) 12 else 1 })
        val headerFont = FontFactory.getFont(defaultFont, Defaults.fontSizeF, Font.BOLD, BaseColor.BLACK)
        table.addCell(
            text = "Name",
            font = headerFont,
            alignment = Element.ALIGN_CENTER,
            borderTop = false,
            borderLeft = false,
        )
        val months = "JFMAMJJASOND"
        repeat(12) {
            table.addCell(
                text = "${it + 1}\n${months[it]}",
                font = headerFont,
                alignment = Element.ALIGN_CENTER,
                borderTop = false,
            )
        }
    }

    override fun addFooter() {}
    override fun addPageBreak() {}

    override fun addReport(donations: List<Donation>) {
        if (donations.isEmpty()) {
            return
        }
        val cellFont = FontFactory.getFont(defaultFont, Defaults.fontSizeF, BaseColor.BLACK)
        table.addCell(
            text = donations.first().name,
            font = cellFont,
            alignment = Element.ALIGN_RIGHT,
            bgColor = rowBG,
        )
        repeat(12) { month ->
            val donationCount = donations.count { it.month == month + 1 }
            table.addCell(
                image = checkmark.takeIf { donationCount > 0 },
                text = "",
                font = cellFont,
                alignment = Element.ALIGN_CENTER,
                bgColor = rowBG,
            )
        }
        rowBG = !rowBG
    }

    override fun writeToFile(): String {
        document.add(table)
        document.close()
        println("pdf written successfully")
        return outputPath
    }

    private fun PdfPTable.addCell(
        text: String = "",
        font: Font? = null,
        image: Image? = null,
        borderWidth: Float = 0.5f,
        padding: Float = 4f,
        alignment: Int = Element.ALIGN_LEFT,
        bgColor: Boolean = false,
        borderTop: Boolean = true,
        borderBottom: Boolean = true,
        borderLeft: Boolean = true,
        borderRight: Boolean = true,
    ) {
        addCell(PdfPCell().apply {
            borderWidthTop = if (borderTop) borderWidth else 0f
            borderWidthBottom = if (borderBottom) borderWidth else 0f
            borderWidthLeft = if (borderLeft) borderWidth else 0f
            borderWidthRight = if (borderRight) borderWidth else 0f
            setPadding(padding)
            when {
                image != null -> {
                    setImage(image)
                    fixedHeight = 10f
                }

                font != null -> phrase = Phrase(text, font)
            }
            horizontalAlignment = alignment
            grayFill = if (bgColor) 0.8f else 1f
        })
    }
}