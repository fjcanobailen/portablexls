package com.edumarketer.data;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import net.sf.jsefa.Deserializer;
import net.sf.jsefa.csv.CsvIOFactory;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.util.*;

/**
 * Created by fjcano on 16/06/2017.
 */
public class Export {
    public static void perform(String fileName, Queue<String> summary, Queue<String> data) throws DocumentException, IOException {

        // Read template
        File file = new File(fileName);

        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook wb = new HSSFWorkbook(fs, true);
        HSSFSheet ws = wb.getSheetAt(0);
        Iterator<Row> rowIterator = ws.iterator();

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream("output.pdf"));
        document.open();
        PdfPTable table = new PdfPTable(10);
        //table.setTotalWidth(710f);//table size
        //table.setLockedWidth(true);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);//both are used to mention the space from heading
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        java.util.List<HSSFPictureData> allPictures = wb.getAllPictures();
        HSSFPictureData hssfPictureData = allPictures.get(0);

        Image img = Image.getInstance(hssfPictureData.getData());
        img.scaleToFit(350, 350);

        // Number of row summary
        int rowSummary = 11;
        int colSpamSummary = 9;

        // Template + Summary
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if (cell.getCellTypeEnum() == CellType.STRING) {
                    if (rowSummary > 0) {
                        Font f = new Font(Font.FontFamily.COURIER, 8, Font.NORMAL, GrayColor.BLACK);
                        // Key
                        PdfPCell pdfPKeyCell = new PdfPCell(new Phrase(cell.getStringCellValue(), f));
                        pdfPKeyCell.setBorder(PdfPCell.NO_BORDER);
                        //pdfPKeyCell.setColspan(colSpamSummary);
                        table.addCell(pdfPKeyCell);
                        // Value
                        String value = summary.poll();
                        PdfPCell pdfPValueCell = new PdfPCell(new Phrase(value, f));
                        pdfPValueCell.setBorder(PdfPCell.NO_BORDER);
                        pdfPValueCell.setColspan(colSpamSummary);
                        table.addCell(pdfPValueCell);

                        rowSummary -= 1;
                    } else {
                        Font f = new Font(Font.FontFamily.COURIER, 8, Font.NORMAL, GrayColor.WHITE);
                        PdfPCell pdfPCell = new PdfPCell(new Phrase(cell.getStringCellValue(), f));
                        pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfPCell.setBorder(PdfPCell.NO_BORDER);
                        pdfPCell.setBackgroundColor(GrayColor.BLACK);
                        table.addCell(pdfPCell);
                    }
                }
            }
        }

        // Data
        for (String d : data) {
            Font f = new Font(Font.FontFamily.COURIER, 8, Font.NORMAL, GrayColor.BLACK);
            PdfPCell pdfPKeyCell = new PdfPCell(new Phrase(d, f));
            pdfPKeyCell.setBorder(PdfPCell.NO_BORDER);
            table.addCell(pdfPKeyCell);
        }

        document.add(img);
        document.add(table);

        document.close();
    }

    public static Queue<String> readSummary(String fileName) throws IOException {
        Queue<String> queue = new LinkedList<String>();
        BufferedReader rd = new BufferedReader(new FileReader(fileName));
        String inputLine;
        StringBuilder builder = new StringBuilder();
        //Store the contents of the file to the StringBuilder.
        while ((inputLine = rd.readLine()) != null)
            builder.append(inputLine);
        Deserializer deserializer = CsvIOFactory.createFactory(Summary.class).createDeserializer();
        StringReader reader = new StringReader(builder.toString());
        deserializer.open(reader);
        Summary summary = deserializer.next();
        queue.add(summary.getInformeCurso());
        queue.add(summary.getSolicitante());
        queue.add(summary.getFechaInicio());
        queue.add(summary.getExpediente());
        queue.add(summary.getFechaFinal());
        queue.add(summary.getAccion());
        queue.add(summary.getAlumnosIniciales());
        queue.add(summary.getGrupo());
        queue.add(summary.getAlumnosActuales());
        queue.add(summary.getFormador());
        deserializer.close(true);
        return queue;
    }

    public static Queue readData(String fileName) throws IOException {
        Queue<String> queue = new LinkedList<String>();
        BufferedReader rd = new BufferedReader(new FileReader(fileName));
        String inputLine;
        StringBuilder builder = new StringBuilder();
        while ((inputLine = rd.readLine()) != null)
            builder.append(inputLine + '\n');
        Deserializer deserializer = CsvIOFactory.createFactory(Data.class).createDeserializer();
        StringReader reader = new StringReader(builder.toString());
        deserializer.open(reader);
        while (deserializer.hasNext()) {
            Data data = deserializer.next();
            queue.add(data.getN());
            queue.add(data.getApellidos());
            queue.add(data.getNombre());
            queue.add(data.getNif());
            queue.add(data.getTelefono());
            queue.add(data.getEmail());
            queue.add(data.getPrimeraConexion());
            queue.add(data.getUltimaConexion());
            queue.add(data.getPorcentajeTotalRealizado());
            queue.add(data.getObservaciones());
        }
        deserializer.close(true);
        return queue;
    }

    public static void main(String[] args) throws IOException, DocumentException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Queue summary = readSummary(classLoader.getResource("summary.csv").getFile());
        Queue data = readData(classLoader.getResource("data.csv").getFile());
        perform(classLoader.getResource("template.xls").getFile(), summary, data);
    }

}

