package com.ibm.ecm.mm.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GenerationManager {
	public static byte[] generate(int documentClassId) {		
		try {		
			Connection conn = ConnectionManager.getConnection("generate");
			Statement stmt = conn.createStatement();
	        String query = "SELECT Identified_Document_Instance.id,"
					    + "      Identified_Document_Instance.server,"
						+ "      Identified_Document_Instance.volume,"
						+ "      Identified_Document_Instance.path,"
						+ "      Identified_Document_Instance.name,"
						+ "      Metadata_Property.filenet_class,"
						+ "      Metadata_Value.value,"
						+ "      Document_Class.name,"
						+ "      Document.name,"
						+ "      Team.name,"
						+ "      Team.department,"
						+ "      IG_Security_Class.name,"
						+ "      Document.id"
						+ " FROM Identified_Document_Instance"
						+ " LEFT JOIN Metadata_Value"
						+ "   ON Identified_Document_Instance.id = Metadata_Value.identified_document_instance_id"
						+ "  AND Identified_Document_Instance.document_id = Metadata_Value.document_id"
						+ " LEFT JOIN Metadata_Extraction_Rule"
						+ "   ON Metadata_Value.metadata_extraction_rule_id = Metadata_Extraction_Rule.id"
						+ " LEFT JOIN Metadata_Property"
						+ "   ON Metadata_Extraction_Rule.metadata_property_id = Metadata_Property.id"
						+ " LEFT JOIN Document"
						+ "   ON Identified_Document_Instance.document_id = Document.id"
						+ " LEFT JOIN Document_Class"
						+ "   ON Document.document_class_id = Document_Class.id"
						+ " LEFT JOIN Team"
						+ "   ON Document.team_id = Team.id"
						+ " LEFT JOIN IG_Security_Class"
						+ "   ON Document.ig_security_class_id = IG_Security_Class.id "
						+ "WHERE Identified_Document_Instance.snapshot_deleted IS NULL";
		    
	        if (documentClassId != 0) {
	        	 query += " AND Document_Class.id = " + documentClassId;
	        }
	        
	        query += " ORDER BY Identified_Document_Instance.id";
	        
	        ResultSet rs = stmt.executeQuery(query);
	        
	        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();	        
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			long lastMigrationId = 0;
			int lastDocumentId = 0;
			Element Lastelement = null;
			
			
			Element rootElement = doc.createElement("data-set");
			doc.appendChild(rootElement);
			
			while ( rs.next() ) {
				
				
				 Long migrationId = rs.getLong(1);
				 String source = "\\\\" + rs.getString(2) + "\\" + rs.getString(3) + "\\";
				 source += rs.getNString(4) == null ? rs.getNString(5) : rs.getNString(4).replace("/", "\\") + "\\" + rs.getNString(5);
				 String metadataName = rs.getString(6);
				 String metadataValue = rs.getString(7);
				 String document = rs.getString(8).replace(" ", "_");
				 String contentType = rs.getString(9);
				 String owningBu = rs.getString(10);
				 String owningDept = rs.getString(11);
				 String secClass = rs.getString(12);
				 int documentId = rs.getInt(13);
	        	
	        	if (migrationId == lastMigrationId && lastDocumentId==documentId) {
	    			if (metadataName != null) {
						Element elementOfMetadata  = doc.createElement(metadataName);
		    			elementOfMetadata.setTextContent(metadataValue);
		    			Lastelement.appendChild(elementOfMetadata);
	    			}
				}
	        	else {
	        		//create new document element 
	        		
	    			Element docElement = doc.createElement("DocumentInstance");
	    			rootElement.appendChild(docElement);
	    			
	    			//Source
	    			Element element = doc.createElement("Source");
					element.appendChild(doc.createCDATASection(source));
					docElement.appendChild(element);
					
					//Destination
					element = doc.createElement("Destination");
					element.setTextContent("\\CPA");
					docElement.appendChild(element);
					
					//DocumentClass
					element = doc.createElement("Document");
					element.setTextContent(document);
					docElement.appendChild(element);
					
					//BusinessMetadata
					element = doc.createElement("BusinessMetadata");
					docElement.appendChild(element);
					
					//Migration ID
					Element elementOfMetadata = doc.createElement("MigrationID");
	    			elementOfMetadata.setTextContent(migrationId.toString());
	    			element.appendChild(elementOfMetadata);	

					//Security Class
					elementOfMetadata = doc.createElement("Security_Classification_IG");
	    			elementOfMetadata.setTextContent(secClass);
	    			element.appendChild(elementOfMetadata);			    			

					//SPI
					elementOfMetadata = doc.createElement("SPI_IG");
	    			elementOfMetadata.setTextContent(" ");
	    			element.appendChild(elementOfMetadata);	
					
					//owningDept
					elementOfMetadata = doc.createElement("Owning_Dept_IG");
	    			elementOfMetadata.setTextContent(owningDept);
	    			element.appendChild(elementOfMetadata);	
	    			
					//Owning_Business_Unit
					elementOfMetadata = doc.createElement("Owning_Business_Unit");
	    			elementOfMetadata.setTextContent(owningBu);
	    			element.appendChild(elementOfMetadata);	

					//Content_Type
					elementOfMetadata = doc.createElement("Content_Type");
	    			elementOfMetadata.setTextContent(contentType);
	    			element.appendChild(elementOfMetadata);	
	    			
					//Metadata
	    			if (metadataName != null) {
						elementOfMetadata = doc.createElement(metadataName);
		    			elementOfMetadata.setTextContent(metadataValue);
		    			element.appendChild(elementOfMetadata);		
	    			}
	    			
	    			Lastelement = element;
	    			lastMigrationId = migrationId;
	    			lastDocumentId = documentId;
	        	}	
	        }
			
			//String outputEncoding = "UTF-16";
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			//transformer.setOutputProperty(OutputKeys.ENCODING, outputEncoding);
			
			StreamResult result =  new StreamResult(bos);//new OutputStreamWriter(bos, outputEncoding));
			
			transformer.transform(domSource, result);
			
			return bos.toByteArray();
	    }
		catch (Exception e) {
			e.printStackTrace();
	    }
		finally {
			ConnectionManager.close("generate");
		}
		return null;
	}
}
