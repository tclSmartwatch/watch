package com.tcl.watch;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

public class FileReadWrite {

	/**
	 * 保存文件覆盖模式
	 * 
	 * @param fileName
	 *            文件名称
	 * @param fileContent
	 *            文件内容
	 */
	public static void savePrivate(Context context, String fileName,
			String fileContent) throws Exception {
		// IO j2se

		// Context.MODE_PRIVATE --私有操作模式：创建出来的文件只能被本应用访问，其它应用无法访问该文件
		// 另外采用私有操作模式创建的文件，写入文件中的内容会覆盖原文件内容
		// 如果想把新写入的内容追加到原文件中，可以使用Context.MODE_APPEND
		FileOutputStream outStream = context.openFileOutput(fileName,
				Context.MODE_PRIVATE);
		outStream.write(fileContent.getBytes());
		outStream.close();
	}

	/**
	 * 保存文件追加模式
	 * 
	 * @param fileName
	 *            文件名称
	 * @param fileContent
	 *            文件内容
	 */
	public static long saveAppend(Context context, String fileName,
			String fileContent) throws Exception {
		// IO j2se

		// Context.MODE_PRIVATE --私有操作模式：创建出来的文件只能被本应用访问，其它应用无法访问该文件
		// 另外采用私有操作模式创建的文件，写入文件中的内容会覆盖原文件内容
		// 如果想把新写入的内容追加到原文件中，可以使用Context.MODE_APPEND
		FileOutputStream outStream = context.openFileOutput(fileName,
				Context.MODE_APPEND);
		byte[] bytes = fileContent.getBytes();
		outStream.write(bytes);
		outStream.close();
		return bytes.length;
	}

	/**
	 * 保存文件
	 * 
	 * @param fileName
	 *            文件名称
	 * @param fileContent
	 *            文件内容
	 */
	public static void saveFile(File fileName, String fileContent)
			throws Exception {
		// IO j2se

		// Context.MODE_PRIVATE --私有操作模式：创建出来的文件只能被本应用访问，其它应用无法访问该文件
		// 另外采用私有操作模式创建的文件，写入文件中的内容会覆盖原文件内容
		// 如果想把新写入的内容追加到原文件中，可以使用Context.MODE_APPEND
		FileOutputStream outStream = new FileOutputStream(fileName);
		outStream.write(fileContent.getBytes());
		outStream.close();
	}

	/**
	 * 读取文件内容
	 * 
	 * @param fileName
	 *            文件名称
	 * @return 文件内容
	 * @throws Exception
	 */
	public static String read(Context context, String fileName)
			throws Exception {
		FileInputStream inStream = context.openFileInput(fileName);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		// 防止数据量过大，超过了1024
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			// 防止每次读取覆盖前一次数据
			outStream.write(buffer, 0, len);
		}

		byte[] data = outStream.toByteArray();

		return new String(data);
	}

	// Context.MODE_APPEND --模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件
	// Context.MODE_WORLD_READABLE和Context.Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件
	// MODE_WORLD_READABLE: 表示当前文件可以被其它应用读取
	// MODE_WORLD_WRITEABLE: 表示当前文件可以被其它应用写入
	// 如果希望文件被其他应用读和写，可以传入：
	// openFileOutput("itcast.txt", Context.MODE_WORLD_READABLE +
	// Context.Context.MODE_WORLD_WRITEABLE);

	// android有一套自己的安全模式，当应用程序(.apk)在安装时系统就会分配给他一个userId
	// 当该应用要去访问其它资源比如文件的时候，就需要userId匹配，默认情况下，任何应用创建的文件，
	// sharePerferences，数据库都应该是私有的(位于/data/data/<package name>/files)，
	// 其它程序无法访问，除非在创建时指定了Context.MODE_WORLD_READABLE或者
	// Context.Context.MODE_WORLD_WRITEABLE，只有这样其它程序才能正确访问

	/**
	 * 读取文件内容
	 * 
	 * @param fileName
	 *            文件名称
	 * @return 文件内容
	 * @throws Exception
	 */
	public static String readFile(File file) throws Exception {
		FileInputStream inStream = new FileInputStream(file);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		// 防止数据量过大，超过了1024
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			// 防止每次读取覆盖前一次数据
			outStream.write(buffer, 0, len);
		}

		byte[] data = outStream.toByteArray();

		return new String(data);
	}

	/**
	 * 判断缓存文件夹是否存在如果存在?返回文件夹路径，如果不存在新建文件夹并返回文件夹路径
	 * 
	 * @param 缓存文件夹
	 */
	public static String isCacheFileIsExit(Context context, String cache) {
		String filePath = "";
		String rootpath = "";
		File file = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			rootpath = Environment.getExternalStorageDirectory().toString();
			filePath = rootpath + cache;
			file = new File(filePath);
		} else {
			file = context.getCacheDir();
		}

		if (!file.exists()) {
			file.mkdirs();
		}

		return filePath;
	}

	/**
	 * 获取文件夹大小
	 * 
	 * @param file
	 *            File实例
	 * @return long 单位为M
	 * @throws Exception
	 */
	public static long getFolderSize(File file) throws Exception {
		long size = 0;
		File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isDirectory()) {
				size = size + getFolderSize(fileList[i]);
			} else {
				size = size + fileList[i].length();
			}
		}

		// Log.e("szie", new DecimalFormat("###.00").format((double) size /
		// 1048576));// 3.14
		return size;
	}

	/**
	 * 删除文件，文件夹
	 * 
	 * @param file
	 */

	public static void RecursionDeleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				RecursionDeleteFile(f);
			}
			file.delete();
		}
	}

	public static String ifExictImg(Context context, String bitmapName,
			String loadString) {
		int noExictFlag = 0;

		// 使用异步图片加载对象，加载图片
		String mFileDir = FileReadWrite.isCacheFileIsExit(context, loadString);
		/**
		 * 加上一个对本地缓存的查找
		 */

		File cacheDir = new File(mFileDir);
		File[] cacheFiles = cacheDir.listFiles();
		int i = 0;
		if (cacheFiles != null)
			for (; i < cacheFiles.length; i++) {
				if (bitmapName.equals(cacheFiles[i].getName())) {
					noExictFlag = 1;
					break;
				}
			}

		if (noExictFlag == 1) {
			// Log.e("adf", mFileDir + "/" + bitmapName);
			return mFileDir + "/" + bitmapName;
		} else {
			return null;
		}
	}

	public static ArrayList<String> readFileName(File file) {

		if (file == null) {
			return null;
		}
		ArrayList<String> arrayList = new ArrayList<String>();
		if (file.isFile()) {
			arrayList.add(file.getName());
		} else if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			for (File f : childFile) {
				arrayList.add(f.getName());
			}

		}
		return arrayList;

	}

	public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
	public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
	public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
	public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值

	/**
	 * 获取文件指定文件的指定单位的大小
	 * 
	 * @param filePath
	 *            文件路径
	 * @param sizeType
	 *            获取大小的类型1为B、2为KB、3为MB、4为GB
	 * @return double值的大小
	 */
	public static double getFileOrFilesSize(String filePath, int sizeType) {
		File file = new File(filePath);
		long blockSize = 0;
		try {
			if (file.isDirectory()) {
				blockSize = getFileSizes(file);
			} else {
				blockSize = getFileSize(file);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return FormetFileSize(blockSize, sizeType);
	}

	/**
	 * 获取文件指定文件的指定单位的大小
	 * 
	 * @param filePath
	 *            文件路径
	 */
	public static long getFileOrFilesSize(String filePath) {
		File file = new File(filePath);
		long blockSize = 0;
		try {
			if (file.isDirectory()) {
				blockSize = getFileSizes(file);
			} else {
				blockSize = getFileSize(file);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return blockSize;
	}

	/**
	 * 调用此方法自动计算指定文件或指定文件夹的大小
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 计算好的带B、KB、MB、GB的字符串
	 */
	public static String getAutoFileOrFilesSize(String filePath) {
		File file = new File(filePath);


		long blockSize = 0;
		try {
			if (file.isDirectory()) {
				blockSize = getFileSizes(file);
			} else {
				blockSize = getFileSize(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FormetFileSize(blockSize);
	}

	/**
	 * 获取指定文件大小
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	private static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
			fis.close();
		} else {
			file.createNewFile();

		}
		return size;
	}

	/**
	 * 获取指定文件夹
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	private static long getFileSizes(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSizes(flist[i]);
			} else {
				size = size + getFileSize(flist[i]);
			}
		}
		return size;
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return
	 */
	public static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0B";
		if (fileS == 0) {
			return wrongSize;
		}
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}

	/**
	 * 转换文件大小,指定转换的类型
	 * 
	 * @param fileS
	 * @param sizeType
	 * @return
	 */
	private static double FormetFileSize(long fileS, int sizeType) {
		DecimalFormat df = new DecimalFormat("#.00");
		double fileSizeLong = 0;
		switch (sizeType) {
		case SIZETYPE_B:
			fileSizeLong = Double.valueOf(df.format((double) fileS));
			break;
		case SIZETYPE_KB:
			fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
			break;
		case SIZETYPE_MB:
			fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
			break;
		case SIZETYPE_GB:
			fileSizeLong = Double.valueOf(df
					.format((double) fileS / 1073741824));
			break;
		default:
			break;
		}
		return fileSizeLong;
	}

	/**
	 * type 0返回总内存大小，1返回剩下可用大小
	 * 
	 * @return
	 */
	public static long getMemory(int type) {
		/** 获取存储卡路径 */
		File sdcardDir = Environment.getExternalStorageDirectory();
		/** StatFs 看文件系统空间使用情况 */
		StatFs statFs = new StatFs(sdcardDir.getPath());
		/** Block 的 size */
		Long blockSize = (long) statFs.getBlockSize();
		/** 总 Block 数量 */
		Long totalBlocks = (long) statFs.getBlockCount();
		/** 已使用的 Block 数量 */
		Long availableBlocks = (long) statFs.getAvailableBlocks();

		if (type == 0) {
			return blockSize * totalBlocks;
		} else {
			return blockSize * availableBlocks;
		}

	}
}

