# QR CODE

> Qr 생성기, 물론 다양한 타입의 바코드 생성도 가능

### Dependency

- com.google.zxing을 사용
- [zxing](https://github.com/zxing/zxing)
- [barcode-generator](https://github.com/DongGeon0908/barcode-generator)

```groovy
implementation("com.google.zxing:core:3.5.1")
implementation("com.google.zxing:javase:3.5.1")
```

### Barcode Type

```java
public enum BarcodeFormat {

    /** Aztec 2D barcode format. */
    AZTEC,

    /** CODABAR 1D format. */
    CODABAR,

    /** Code 39 1D format. */
    CODE_39,

    /** Code 93 1D format. */
    CODE_93,

    /** Code 128 1D format. */
    CODE_128,

    /** Data Matrix 2D barcode format. */
    DATA_MATRIX,

    /** EAN-8 1D format. */
    EAN_8,

    /** EAN-13 1D format. */
    EAN_13,

    /** ITF (Interleaved Two of Five) 1D format. */
    ITF,

    /** MaxiCode 2D barcode format. */
    MAXICODE,

    /** PDF417 format. */
    PDF_417,

    /** QR Code 2D barcode format. */
    QR_CODE,

    /** RSS 14 */
    RSS_14,

    /** RSS EXPANDED */
    RSS_EXPANDED,

    /** UPC-A 1D format. */
    UPC_A,

    /** UPC-E 1D format. */
    UPC_E,

    /** UPC/EAN extension format. Not a stand-alone format. */
    UPC_EAN_EXTENSION

}

```

### BarcodeGenerator

```kotlin
package com.goofy.qrcode.util

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageConfig
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream

class BarcodeGenerator {
    companion object {
        private val logger = mu.KotlinLogging.logger {}

        fun generateBarCode(barcode: BarcodeModel): ByteArray {
            return runCatching {
                val outputStream = ByteArrayOutputStream()

                val bitMatrix = QRCodeWriter().encode(
                    barcode.content,
                    barcode.type,
                    barcode.width,
                    barcode.height
                )
                val matrixToImageConfig = MatrixToImageConfig(barcode.onColor, barcode.offColor)

                MatrixToImageWriter.writeToStream(
                    bitMatrix,
                    barcode.fileFormat.name,
                    outputStream,
                    matrixToImageConfig
                )

                outputStream.toByteArray()
            }.getOrElse {
                logger.error { "error > ${it.message}" }
                throw BarcodeGeneratorException()
            }
        }
    }

    data class BarcodeModel(
        /** Barcode Type*/
        val type: BarcodeFormat,
        /** BarCode에 들어갈 데이터 */
        val content: String,
        /** BarCode 이미지 너비 */
        val width: Int,
        /** BarCode 이미지 높이 */
        val height: Int,
        /** BarCode 데이터 색상 */
        val onColor: Int,
        /** BarCode 데이터 제외 색상 */
        val offColor: Int,
        /** Output File Format*/
        val fileFormat: FileFormat
    )

    class BarcodeGeneratorException : RuntimeException()

    enum class FileFormat {
        JPEG, PNG
    }
}


```

- Qr Code를 생성할때, stream하게 사용 가능하고, 혹은 파일로 생성시킬 수 있음 상황에 따라서 선택하여 진행
- 현재 Generator에서는 다양한 타입의 바코드를 생성할 수 있다. (일반 바코드 및 다양한 형태의 바코드 이미지 생성 가능)
