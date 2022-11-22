package com.clouddrive.modules.file.service;

import java.io.FileInputStream;
import java.io.IOException;

public interface DownloadService {

    FileInputStream DownloadFile(String downloadId) throws IOException;
}
