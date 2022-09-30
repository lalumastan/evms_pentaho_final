package sqltutorial.evms.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import sqltutorial.evms.util.ReportHelper;
import sqltutorial.evms.util.ReportHelper.OutputType;

@Controller
@RequestMapping("/report")
public class ReportController {

	@GetMapping(value = "/employee_vaccine/{format}")
	public ResponseEntity<InputStreamResource> vaccineReport(@PathVariable("format") String format) {
		// Generate the report processor for the specified output type
		OutputType outputType = OutputType.HTML;

		switch (format) {
		case "pdf": {
			outputType = OutputType.PDF;
			break;
		}

		case "xlsx": {
			outputType = OutputType.XLSX;
			break;
		}

		case "csv": {
			outputType = OutputType.CSV;
			break;
		}

		case "rtf": {
			outputType = OutputType.RTF;
			break;
		}

		case "txt": {
			outputType = OutputType.TXT;
			break;
		}
		}

		return new ReportHelper().generateReport(outputType);
	}
}
