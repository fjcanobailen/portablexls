package com.edumarketer.data;

import au.com.bytecode.opencsv.CSVReader;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import net.sf.jsefa.Deserializer;
import net.sf.jsefa.csv.CsvIOFactory;
import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by fjcano on 16/06/2017.
 */
public class Export {

    public static final int SHEET_INDEX = 0;
    public static final int PDFTABLE_NUM_COLUMS = 10;
    public static final int PDFTABLE_WIDTH_PERCENTAGE = 100;
    public static final float PDFTABLE_SPACING_BEFORE = 10f;
    public static final int IMAGE_INDEX = 0;
    public static final int IMAGE_SCALE_WIDTH = 350;
    public static final int IMAGE_SCALE_HEIGHT = 350;
    public static final int FONT_SIZE = 8;
    public static final int SUMMARY_ROWS = 10;
    public static final int SUMMARY_COL_SPAM = 9;
    private static final Font DATA_FONT = new Font(Font.FontFamily.COURIER, FONT_SIZE, Font.NORMAL, GrayColor.BLACK);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.COURIER, FONT_SIZE, Font.NORMAL, GrayColor.WHITE);

    static final Logger logger = LogManager.getLogger(Export.class.getName());

    /**
     * Perform a export to PDF from a template
     *
     * @param templateFileName XLS template file name path
     * @param summary          Summary in queue storage with SUMMARY_ROWS size. It simplify the tree document creation
     * @param dataFileName     Data file name path
     * @param outputFileName   PDF file name path
     * @throws DocumentException
     * @throws IOException
     */
    public static void perform(String templateFileName,
                               Queue<String> summary,
                               ImmutablePair<Integer, Integer> indexes,
                               String dataFileName,
                               String outputFileName) throws DocumentException, IOException {
        // Read template
        File file = new File(templateFileName);

        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook wb = new HSSFWorkbook(fs, true);
        HSSFSheet ws = wb.getSheetAt(SHEET_INDEX);
        Iterator<Row> rowIterator = ws.iterator();

        // PDF
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(outputFileName));
        document.open();
        PdfPTable table = new PdfPTable(PDFTABLE_NUM_COLUMS);
        table.setWidthPercentage(PDFTABLE_WIDTH_PERCENTAGE);
        table.setSpacingBefore(PDFTABLE_SPACING_BEFORE);//both are used to mention the space from heading
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        // Get Image from template
        java.util.List<HSSFPictureData> allPictures = wb.getAllPictures();
        if (allPictures.size() > 0) {
            HSSFPictureData hssfPictureData = allPictures.get(IMAGE_INDEX);
            Image img = Image.getInstance(hssfPictureData.getData());
            img.scaleToFit(IMAGE_SCALE_WIDTH, IMAGE_SCALE_HEIGHT);
            document.add(img);
        }

        // Number of rows summary
        int rowSummary = 0;
        if (summary == null) {
            logger.trace("No summary");
        } else {
            rowSummary = SUMMARY_ROWS;
            logger.trace("Number of summary rows " + rowSummary);
        }

        // Start from 0 index
        int headerColumnSize = (indexes.right - indexes.left) + 1;
        logger.debug("Header column size: " + headerColumnSize);
        // Template [Summary + Data headers]
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                String cellValue = cell.getStringCellValue();
                if (cell.getCellTypeEnum() == CellType.STRING && StringUtils.isNotBlank(cellValue)) {
                    if (rowSummary > 0) {
                        // Key
                        logger.trace("Creating cell key summary from template: " + cellValue);
                        PdfPCell pdfPKeyCell = new PdfPCell(new Phrase(cellValue, DATA_FONT));
                        pdfPKeyCell.setBorder(PdfPCell.NO_BORDER);
                        table.addCell(pdfPKeyCell);
                        // Value
                        String value = summary.poll();
                        logger.trace("Creating cell value summary from template: " + value);
                        PdfPCell pdfPValueCell = new PdfPCell(new Phrase(value, DATA_FONT));
                        pdfPValueCell.setBorder(PdfPCell.NO_BORDER);
                        pdfPValueCell.setColspan(SUMMARY_COL_SPAM);
                        table.addCell(pdfPValueCell);
                        rowSummary -= 1;
                    } else {
                        if (cell.getColumnIndex() < indexes.left || cell.getColumnIndex() > indexes.right) {
                            logger.trace("Skipping cell data header from template: " + cellValue);
                        } else {
                            logger.trace("Creating cell data header from template: " + cellValue);
                            PdfPCell pdfPCell = new PdfPCell(new Phrase(cellValue, HEADER_FONT));
                            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfPCell.setBorder(PdfPCell.NO_BORDER);
                            pdfPCell.setBackgroundColor(GrayColor.BLACK);
                            table.addCell(pdfPCell);
                        }
                    }
                }
            }
        }
        // Fix data headers
        int numberOfBlanks = PDFTABLE_NUM_COLUMS - headerColumnSize;
        if (numberOfBlanks > 0) {
            logger.trace("Creating blank cell data header ");
            PdfPCell pdfPValueCell = new PdfPCell(new Phrase("", DATA_FONT));
            pdfPValueCell.setBorder(PdfPCell.NO_BORDER);
            pdfPValueCell.setColspan(numberOfBlanks);
            table.addCell(pdfPValueCell);
        }
        // Data
        CSVReader reader = new CSVReader(new FileReader(dataFileName), ';');
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            logger.trace("Current data line size: " + nextLine.length);
            int ci = 0;
            for (String c : nextLine) {
                if (ci < indexes.left || ci > indexes.right) {
                    logger.trace("Skipping cell data: " + c);
                } else {
                    logger.trace("Creating cell data: " + c);
                    Font f = new Font(Font.FontFamily.COURIER, FONT_SIZE, Font.NORMAL, GrayColor.BLACK);
                    PdfPCell pdfPKeyCell = new PdfPCell(new Phrase(nextLine[ci], f));
                    pdfPKeyCell.setBorder(PdfPCell.NO_BORDER);
                    table.addCell(pdfPKeyCell);
                }
                ci += 1;
            }
            // Add blank data
            if (numberOfBlanks > 0) {
                logger.trace("Creating blank cell data ");
                PdfPCell pdfPValueCell = new PdfPCell(new Phrase("", DATA_FONT));
                pdfPValueCell.setBorder(PdfPCell.NO_BORDER);
                pdfPValueCell.setColspan(numberOfBlanks);
                table.addCell(pdfPValueCell);
            }
        }
        document.add(table);
        document.close();
    }

    /**
     * From CSV file to Summary object
     *
     * @param fileName CSV file name path
     * @return
     * @throws IOException
     */
    public static Queue<String> readSummary(String fileName) throws IOException {
        Queue<String> queue = new LinkedList<String>();
        BufferedReader rd = new BufferedReader(new FileReader(fileName));
        String inputLine;
        StringBuilder builder = new StringBuilder();
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

    public static ImmutablePair<Integer, Integer> evaluateIndexes(int s, int e) {
        logger.trace("Evaluate s: " + s + ", " + e);
        int c = e - s;
        if (c <= 0 || c > PDFTABLE_NUM_COLUMS)
            return null;
        else
            return ImmutablePair.of(s, e);
    }

    public static void main(String[] args) throws IOException, DocumentException {

        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        options.addOption("t", "template", true, "template file path.");
        options.addOption("s", "summary", true, "summary file path.");
        options.addOption("d", "data", true, "data file path.");
        options.addOption("i", "startEndIndexData", true, "start (from 0),end column indexes.");
        options.addOption("o", "output", true, "output file path.");

        try {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("t") && line.hasOption("d") && line.hasOption("o")) {
                // Evaluate indexes
                ImmutablePair<Integer, Integer> indexes = new ImmutablePair<Integer, Integer>(0,
                        PDFTABLE_NUM_COLUMS - 1);
                if (line.hasOption("i")) {
                    String[] indexesOptionValues = line.getOptionValue("i").split(",");
                    indexes = evaluateIndexes(Integer.parseInt(indexesOptionValues[0]),
                            Integer.parseInt(indexesOptionValues[1]));
                    if (indexes == null) {
                        System.out.println("Review indexes!");
                        System.exit(0);
                    }
                    logger.trace("Set indexes for data: " + indexes);
                }
                Queue<String> summary = null;
                if (line.hasOption("s"))
                    summary = readSummary(line.getOptionValue("s"));
                perform(line.getOptionValue("t"), summary, indexes, line.getOptionValue("d"), line.getOptionValue("o"));
            } else {
                System.out.println("USAGE: java -jar target/portablexls-jar-with-dependencies.jar -t [templateFilePath] -s [Optional. summaryFilePath] -i [Optional. startIndex,endIndex] -d [dataFilePath] -o [outputFilePath]");
            }
        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        }
    }
}

