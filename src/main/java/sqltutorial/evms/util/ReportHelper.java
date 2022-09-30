package sqltutorial.evms.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.PageableTextOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.TextFilePrinterDriver;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.RTFReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ReportHelper {
	private final static String EMPLOYEE_VACCINATION_REPORT = "employee-vaccination-report";

	private MasterReport masterReport;

	/**
	 * The supported output types for this sample
	 */
	public static enum OutputType {
		PDF, XLSX, CSV, RTF, TXT, HTML
	}

	protected String reportId;

	/**
	 * Performs the basic initialization required to generate a report
	 */
	public ReportHelper() {
		this(EMPLOYEE_VACCINATION_REPORT);
	}

	/**
	 * Performs the basic initialization required to generate a report
	 */
	public ReportHelper(String reportId) {
		this.reportId = reportId;

		// Initialize the reporting engine
		ClassicEngineBoot boot = ClassicEngineBoot.getInstance();
		if (!boot.isBootDone())
			boot.start();

		try {
			// Using the classloader, get the URL to the reportDefinition file
			final URL reportDefinitionURL = ReportHelper.class.getResource("/" + reportId + ".prpt");

			// Parse the report file
			final ResourceManager resourceManager = new ResourceManager();
			resourceManager.registerDefaults();
			final Resource directly = resourceManager.createDirectly(reportDefinitionURL, MasterReport.class);

			masterReport = (MasterReport) directly.getResource();
		} catch (ResourceException e) {
			e.printStackTrace();
		}
	}

	public ResponseEntity<InputStreamResource> generateReport(OutputType outputType) {
		String reportFormat = outputType.name().toLowerCase(), file_name = reportId + "." + reportFormat;

		MediaType mediaType = MediaType.TEXT_HTML;

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			// Generate the report processor for the specified output type
			switch (outputType) {
			case PDF: {
				mediaType = MediaType.APPLICATION_PDF;
				PdfReportUtil.createPDF(masterReport, outputStream);
				break;
			}

			case XLSX: {
				mediaType = MediaType.APPLICATION_OCTET_STREAM;
				ExcelReportUtil.createXLSX(masterReport, outputStream);
				break;
			}

			case CSV: {
				mediaType = MediaType.TEXT_HTML;
				CSVReportUtil.createCSV(masterReport, outputStream, null);
				break;
			}

			case RTF: {
				mediaType = MediaType.APPLICATION_OCTET_STREAM;
				RTFReportUtil.createRTF(masterReport, outputStream);
				break;
			}

			case TXT: {
				mediaType = MediaType.TEXT_PLAIN;
				final TextFilePrinterDriver pc = new TextFilePrinterDriver(outputStream, 15, 10);
				final PageableTextOutputProcessor outputProcessor = new PageableTextOutputProcessor(pc,
						masterReport.getConfiguration());
				final PageableReportProcessor proc = new PageableReportProcessor(masterReport, outputProcessor);
				proc.processReport();
				proc.close();
				outputStream.close();
				break;
			}

			default: {
				mediaType = MediaType.TEXT_HTML;
				HtmlReportUtil.createStreamHTML(masterReport, outputStream);
				break;
			}
			}

			String contentDisposition = "; filename=\"" + file_name + "\"";
			contentDisposition = (reportFormat.equals(OutputType.HTML.name().toLowerCase()) ? "inline" : "attachment")
					+ contentDisposition;

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", contentDisposition);

			return ResponseEntity.ok().headers(headers).contentType(mediaType)
					.body(new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray())));
		} catch (Exception e) {
			String errMsg = "Unable to view the report in " + reportFormat + " format: " + e.getMessage() + ".";
			e.printStackTrace();

			return ResponseEntity.ok().contentType(mediaType)
					.body(new InputStreamResource(new ByteArrayInputStream(errMsg.getBytes())));
		} finally {
			if (outputStream != null)
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}