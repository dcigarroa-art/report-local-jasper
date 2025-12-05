package com.example.demo;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.json.data.JsonDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		generateReport();
	}

	public static void generateReport() {
		try {
			// Leer datos JSON
			ClassPathResource jsonResource = new ClassPathResource("data.json");
			InputStream jsonInputStream = jsonResource.getInputStream();
			byte[] jsonBytes = jsonInputStream.readAllBytes();
			String jsonContent = new String(jsonBytes);
			jsonInputStream.close();
			
			// Cargar template Jasper
			ClassPathResource jasperResource = new ClassPathResource("ApplicationES.jasper");
			InputStream jasperInput = jasperResource.getInputStream();
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperInput);
			jasperInput.close();
			
			// Configurar parámetros y datasource
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("JSON_DATA", jsonContent);
			
			ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(jsonBytes);
			JsonDataSource dataSource = new JsonDataSource(jsonDataStream);
			
			// Generar reporte
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
			
			// Exportar a PDF
			String outputPath = System.getProperty("user.home") + "/Documents/ApplicationReport.pdf";
			JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
			
			// Mensaje de éxito
			System.out.println("✓ PDF generado exitosamente (" + jasperPrint.getPages().size() + " páginas)");
			System.out.println("  Ubicación: " + outputPath);
			
		} catch (Exception e) {
			System.err.println("✗ Error al generar el reporte:");
			e.printStackTrace();
		}
	}
}
