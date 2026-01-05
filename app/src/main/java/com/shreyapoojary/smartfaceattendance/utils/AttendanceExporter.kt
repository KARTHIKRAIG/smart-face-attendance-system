package com.shreyapoojary.smartfaceattendance.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

data class ExportAttendanceItem(
    val name: String,
    val time: String,
    val status: String
)

object AttendanceExporter {

    fun exportToExcel(
        context: Context,
        date: String,
        attendanceList: List<ExportAttendanceItem>
    ) {
        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Attendance")

            // 🔹 Header Row
            val header = sheet.createRow(0)
            header.createCell(0).setCellValue("Name")
            header.createCell(1).setCellValue("Time")
            header.createCell(2).setCellValue("Status")

            // 🔹 Data Rows
            attendanceList.forEachIndexed { index, item ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(item.name)
                row.createCell(1).setCellValue(item.time)
                row.createCell(2).setCellValue(item.status)
            }

            val fileName = "Attendance_$date.xlsx"

            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                fileName
            )

            val outputStream = FileOutputStream(file)
            workbook.write(outputStream)
            outputStream.close()
            workbook.close()

            Toast.makeText(
                context,
                "Excel exported:\n${file.absolutePath}",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Export failed: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
