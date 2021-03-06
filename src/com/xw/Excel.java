package com.xw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.xw.exception.ExcelException;

public class Excel {
	Workbook m_Workbook;
	File m_File;

	class Sheet {
		org.apache.poi.ss.usermodel.Sheet m_Sheet = m_Workbook.getSheetAt(0);
		public static final String DIV = "|";
		public static final String NEW_LINE = "newline";

		public Sheet(org.apache.poi.ss.usermodel.Sheet m_Sheet) {
			this.m_Sheet = m_Sheet;
		}

		private Row getRow(int row) {
			Row _row = m_Sheet.getRow(row);
			if (_row == null)
				_row = m_Sheet.createRow(row);
			return _row;
		}
		
		public String getName() {
			return m_Sheet.getSheetName();
		}

		public void write(int row, int col, Object value) throws ExcelException {
			write(new int[] { row - 1, col - 1 }, value);
		}

		public void write(int[] location, Object value) throws ExcelException {
			Row _row = getRow(location[0]);
			Cell cell = _row.createCell(location[1]);

			if (value instanceof String) {
				cell.setCellValue((String) value);
			} else if (value instanceof Date) {
				cell.setCellValue((Date) value);
			} else if (value instanceof Boolean) {
				cell.setCellValue((Boolean) value);
			} else if (value instanceof Double) {
				cell.setCellValue((Double) value);
			} else if (value instanceof Float) {
				cell.setCellValue((Float) value + 0.0);
			} else if (value instanceof Integer) {
				cell.setCellValue((Integer) value + 0.0);
			} else
				throw new ExcelException("Cant write with " + value.toString());
		}

		public void writeFormat(int row, int col, String text) throws ExcelException {
			writeFormat(new int[] { row - 1, col - 1 }, text);
		}

		public void writeFormat(int[] location, String text) throws ExcelException {
			int row = location[0];
			int col = location[1];
			String[] row_list = text.split(NEW_LINE);
			for (String each_row : row_list) {
				String[] cell_list = each_row.split("\\" + DIV);
				int col_add = 0;
				Row _row = getRow(row);
				for (String each_text : cell_list) {
					_row.createCell(col + col_add).setCellValue(each_text);
					col_add++;
				}
				row++;
			}
		}

		public Object read(int row, int col) {
			return read(new int[] { row - 1, col - 1 });
		}

		public Object read(int[] location) {
			if (m_Sheet.getLastRowNum() < location[0])
				return null;
			Row _row = getRow(location[0]);
			if (_row.getLastCellNum() < location[1])
				return null;
			Cell cell = _row.getCell(location[1]);
			if(cell==null)return null;
			switch (cell.getCellTypeEnum()) {
			case FORMULA:
			case NUMERIC:
				return cell.getNumericCellValue();
			case STRING:
				return cell.getStringCellValue();
			case BOOLEAN:
				return cell.getBooleanCellValue();
			case BLANK:
			case _NONE:
			case ERROR:
			default:
				return null;
			}
		}

		public int getLastRowNum() {
			return m_Sheet.getLastRowNum();
		}

	}

	public Excel(File file) throws IOException, ExcelException {

		m_File = file;
		String filePath = file.getAbsolutePath();
		if (!m_File.exists())
			throw new ExcelException("File existed");
		if (filePath.endsWith("xls"))
			m_Workbook = new HSSFWorkbook(new FileInputStream(m_File));
		else if (filePath.endsWith("xlsx"))
			m_Workbook = new XSSFWorkbook(new FileInputStream(m_File));
		else
			throw new ExcelException("InValid File " + filePath);

	}

	public static Excel createExcel(String path, boolean overWrite) throws IOException, ExcelException {
		File file = new File(path);
		if (file.exists() && !overWrite)
			throw new ExcelException("File existed");
		if (file.exists())
			file.delete();
		if(!file.createNewFile()) return null;

		Workbook workbook;
		String filePath = file.getAbsolutePath();
		if (filePath .endsWith("xls"))
			workbook = new HSSFWorkbook();
		else
			workbook = new XSSFWorkbook();

		FileOutputStream out = new FileOutputStream(file);
		workbook.write(out);
		out.close();
		workbook.close();
		
		return new Excel(file);
	}

	public Sheet createSheet(String name) {
		return new Sheet(m_Workbook.createSheet(name));
	}

	public Sheet getSheet(String name) {
		return new Sheet(m_Workbook.getSheet(name));
	}

	public List<Sheet> getSheets() {
		ArrayList<Sheet> list = new ArrayList<>();
		for (int i = 0; i < m_Workbook.getNumberOfSheets(); i++)
			list.add(new Sheet(m_Workbook.getSheetAt(i)));
		return list;
	}

	public void save() throws IOException {
		FileOutputStream out = new FileOutputStream(m_File);
		m_Workbook.write(out);
		out.close();
	}

	public void close() throws IOException {
		m_Workbook.close();
	}

	public void closeWithSave() throws IOException {
		save();
		m_Workbook.close();
	}
}
