package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.ReceiptScanResponse;
import com.hcmut.divvy.service.model.ScanReceiptModel;

public interface ReceiptScanService {

    ReceiptScanResponse scanReceipt(ScanReceiptModel model);
}
