package com.rlcc.videolibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

/**
 * 文件工具类
 *
 * @author huangbiao
 * @date 2015年6月29日
 */
public class FileUtils {

    /**
     * 获取可以使用的缓存目录
     *
     * @param context
     * @param uniqueName 目录名称
     * @return
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ? getExternalCacheDir(context).getPath() : context
                .getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取程序外部的缓存目录
     *
     * @param context
     * @return
     */
    public static File getExternalCacheDir(Context context) {
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    /**
     * 判断文件是否为空
     *
     * @date 2015年6月29日
     * @author huangbiao
     */
    public static boolean isFileExists(String path) {
        return new File(path).exists();
    }

    /**
     * 判断是否挂载SD卡
     *
     * @return
     */
    public static boolean isExternalStorageMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 检查并创建目录
     *
     * @param path
     * @date 2015年6月29日
     * @author huangbiao
     */
    public static void checkOrCreateDirectory(String path, boolean isDirectory) {
        File file = new File(path);
        if (!file.exists()) {
            File parentFile = file;
            if (!isDirectory) {
                parentFile = file.getParentFile();
            }
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
        }
    }

    /**
     * 复制文件
     *
     * @param strPathDst 目标文件
     * @param strPathSrc 源文件
     * @date 2015年6月29日
     * @author huangbiao
     */
    public static void copyFile(String strPathDst, String strPathSrc) {
        if (strPathDst != null && !strPathDst.equals(strPathSrc)) {
            FileOutputStream fos = null;
            FileInputStream fis = null;
            try {
                fos = createFileOutputStream(strPathDst);
                fis = new FileInputStream(strPathSrc);
                byte buf[] = new byte[1024];
                int nReadBytes = 0;
                while ((nReadBytes = fis.read(buf, 0, buf.length)) != -1) {
                    fos.write(buf, 0, nReadBytes);
                }
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static FileOutputStream createFileOutputStream(String strPath) throws Exception {
        final File file = new File(strPath);
        try {
            return new FileOutputStream(file);
        } catch (Exception e) {
            final File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                if (fileParent.mkdirs()) {
                    return new FileOutputStream(file);
                }
            }
        }

        return null;
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(File file) {

        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);

                } else {
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param deleteThisPath
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte(s)";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    public static String saveImage(Bitmap bitmap, String name) {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ccapp_file/";
        File fileDir = new File(absolutePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        OutputStream outStream = null;
        String fileName = name;
        if (TextUtils.isEmpty(name)) {
            fileName = System.currentTimeMillis() + "";
        }
        try {
            int size = bitmap.getByteCount();
            File file = new File(fileDir, "S_IMG" + fileName + ".PNG");
            boolean newfile = file.createNewFile();
            outStream = new FileOutputStream(file);
            boolean s = bitmap.compress(CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outStream != null) {
                    outStream.flush();
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static String saveImage(Bitmap bitmap, int size, String name) {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ccapp_file/";
        File fileDir = new File(absolutePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        OutputStream outStream = null;
        try {
            File file = new File(fileDir, "S_IMG" + name + ".PNG");
            boolean newfile = file.createNewFile();
            outStream = new FileOutputStream(file);
            boolean s = bitmap.compress(CompressFormat.PNG, size, outStream);
            outStream.flush();
            outStream.close();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                outStream.flush();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static String saveVideo(String path) {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ccapp_file/";
        File fileDir = new File(absolutePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File srcFile = new File(path);
        String srcName = srcFile.getName();
        File file = new File(fileDir, srcName);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            boolean isNew = file.createNewFile();
            input = new FileInputStream(srcFile);
            output = new FileOutputStream(file);
            byte b[] = new byte[1024 * 5];
            int len = 0;
            while ((len = input.read(b)) != -1) {
                output.write(b, 0, len);
            }
            output.flush();
            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file.getAbsolutePath();
    }

    public static File createImgFile(Context context) {
        String name = SystemClock.elapsedRealtime() + "_pic.jpg";
        String path = FileUtils.getDiskCacheDir(context, "pic").getAbsolutePath();
        FileUtils.checkOrCreateDirectory(path, true);
        File captureFile = new File(path, name);
        return captureFile;
    }
}
