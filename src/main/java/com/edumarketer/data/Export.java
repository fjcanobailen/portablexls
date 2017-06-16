package com.edumarketer.data;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.util.Iterator;

public class Export
{
    public static void test() throws DocumentException, IOException {
        // Read template
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("template.xls").getFile());

        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook wb = new  HSSFWorkbook(fs, true);
        HSSFSheet ws = wb.getSheetAt(0);
        Iterator<Row> rowIterator = ws.iterator();

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("output.pdf"));
        document.open();
        PdfPTable table = new PdfPTable(10);
        table.setTotalWidth(510f);//table size
        table.setLockedWidth(true);
        table.setSpacingBefore(10f);//both are used to mention the space from heading
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        while(rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while(cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if (cell.getCellTypeEnum() == CellType.STRING) {
                    PdfPCell pdfPCell = new PdfPCell(new Phrase(cell.getStringCellValue()));
                    pdfPCell.setBorder(PdfPCell.NO_BORDER);
                    pdfPCell.setColspan(10);
                    table.addCell(pdfPCell);
                    break;
                }
            }
        }
        document.add(table);
        document.close();
    }
    public static void main( String[] args ) throws IOException, DocumentException {
        test();
    }


}

