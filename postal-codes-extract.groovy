#!/usr/bin/env groovy
import org.apache.poi.hssf.usermodel.HSSFWorkbook
@Grapes(
        @Grab('org.apache.poi:poi:3.15-beta1')
)

import org.apache.poi.ss.usermodel.*

class PostalAddress {
    String type
    String name
    String numberOrBuilding
    String postalCode
    String sector
    String postOffice
    String county
    String city

    @Override
    String toString() {
        return new StringBuilder()
                .append(type ?: "").append(" ")
                .append(name ?: "").append(" ")
                .append(numberOrBuilding ?: "").append(" ")
                .append(city ?: "").append(", ")
                .append(sector ?: county ?: "")
                .append(", ")
                .append(postalCode ?: "").append(" ")
                .append(postOffice ?: "")
                .toString()
    }

    static def cellExtract(Row row) {
        return { int cell -> row?.getCell(cell)?.getStringCellValue() };
    }

    static PostalAddress forBucharest(Row row) {
        def stringValue = cellExtract(row)

        return new PostalAddress(
                type: stringValue(0),
                name: stringValue(1),
                numberOrBuilding: stringValue(2),
                postalCode: stringValue(3),
                sector: row?.getCell(4).getNumericCellValue(),
                postOffice: stringValue(5),
                city: 'București'
        )
    }

    static PostalAddress forCity(Row row) {
        def stringValue = cellExtract(row)
        return new PostalAddress(
                county: stringValue(0),
                city: stringValue(1),
                type: stringValue(2),
                name: stringValue(3),
                numberOrBuilding: stringValue(4),
                postalCode: stringValue(5),
        )
    }

    static PostalAddress forTown(Row row) {
        def stringValue = cellExtract(row)
        return new PostalAddress(
                county: stringValue(0),
                city: stringValue(1),
                postalCode: stringValue(3),
        )
    }

}

def file = args[0];
def templateName = args.length == 2 ? args[1] : 'address.tpl'

InputStream inp = new FileInputStream(file);
Workbook wb = new HSSFWorkbook(inp);
Sheet sheet = wb.getSheetAt(0);

Row row = sheet.getRow(2);


def address = PostalAddress.forBucharest(row)
def template = new File(templateName).getText('UTF-8')

def engine = new groovy.text.SimpleTemplateEngine().createTemplate(template)

println("Adresa este: ${engine.make([address: address])}")



