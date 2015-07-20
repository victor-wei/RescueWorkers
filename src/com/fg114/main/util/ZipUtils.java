package com.fg114.main.util;

import java.io.*;
import java.util.zip.*;

/**
 * 压缩工具
 * @author wufucheng
 */
public class ZipUtils {

	private static final String TAG = ZipUtils.class.getName();
	private static final int BUFFER_SIZE = 1024; // 缓存大小

	/**
	 * GZip压缩数据
	 * @param input
	 * @return
	 */
	public static byte[] gZip(byte[] input) {
		ByteArrayOutputStream baos = null;
		GZIPOutputStream gzos = null;
		DataOutputStream dos = null;
		try {
			baos = new ByteArrayOutputStream();
			gzos = new GZIPOutputStream(baos, input.length);
			dos = new DataOutputStream(gzos); // 压缩级别缺省为1级
			dos.write(input);
			dos.flush();
			gzos.close();
			return baos.toByteArray();
		} catch (Exception e) {
			LogUtils.logE(TAG, e);
			return null;
		} finally {
			try {
				if (dos != null) {
					dos.close();
				}
				if (gzos != null) {
					gzos.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (Exception e) {
				LogUtils.logE(TAG, e);
			}
		}
	}

	/**
	 * GZip解压数据
	 * @param input
	 * @return
	 */
	public static byte[] unGZip(byte[] input) {
		ByteArrayOutputStream baos = null;
		ByteArrayInputStream bais = null;
		GZIPInputStream gzis = null;
		DataInputStream dis = null;
		try {
			baos = new ByteArrayOutputStream();
			bais = new ByteArrayInputStream(input);
			gzis = new GZIPInputStream(bais);
			dis = new DataInputStream(gzis);
			byte[] buffer = new byte[BUFFER_SIZE];
			int count = 0;
			while ((count = dis.read(buffer, 0, BUFFER_SIZE)) != -1) {
				baos.write(buffer, 0, count);
				baos.flush();
			}
			return baos.toByteArray();
		} catch (Exception e) {
			LogUtils.logE(TAG, e);
			return null;
		} finally {
			try {
				if (dis != null) {
					dis.close();
				}
				if (gzis != null) {
					gzis.close();
				}
				if (bais != null) {
					bais.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (Exception e) {
				LogUtils.logE(TAG, e);
			}
		}
	}

	/**
	 * Zip压缩数据
	 * @param input
	 * @return
	 */
	public static byte[] zip(byte[] input) {
		ByteArrayOutputStream baos = null;
		ZipOutputStream zos = null;
		try {
			baos = new ByteArrayOutputStream();
			zos = new ZipOutputStream(baos);
			ZipEntry entry = new ZipEntry("zip");
			entry.setSize(input.length);
			zos.putNextEntry(entry);
			zos.write(input);
			zos.flush();
			zos.closeEntry();
			zos.close();
			return baos.toByteArray();
		} catch (Exception e) {
			LogUtils.logE(TAG, e);
			return null;
		} finally {
			try {
				if (zos != null) {
					zos.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (Exception e) {
				LogUtils.logE(TAG, e);
			}
		}
	}

	/**
	 * Zip解压数据
	 * @param input
	 * @return
	 */
	public static byte[] unZip(byte[] input) {
		ByteArrayOutputStream baos = null;
		ByteArrayInputStream bais = null;
		ZipInputStream zis = null;
		try {
			baos = new ByteArrayOutputStream();
			bais = new ByteArrayInputStream(input);
			zis = new ZipInputStream(bais);
			while (zis.getNextEntry() != null) {
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = -1;
				while ((count = zis.read(buffer, 0, buffer.length)) != -1) {
					baos.write(buffer, 0, count);
					baos.flush();
				}
			}
			return baos.toByteArray();
		} catch (Exception e) {
			LogUtils.logE(TAG, e);
			return null;
		} finally {
			try {
				if (zis != null) {
					zis.close();
				}
				if (bais != null) {
					bais.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (Exception e) {
				LogUtils.logE(TAG, e);
			}
		}
	}
}
