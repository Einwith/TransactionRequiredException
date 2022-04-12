package com.lixar.apba.core.php;

import com.lixar.apba.core.php.errors.InvalidClassForPHPExtraction;

public interface PHPMap<K> {
    String toInsertString(K object) throws InvalidClassForPHPExtraction;
    String toUpdateString(K object, String[] updateFields) throws InvalidClassForPHPExtraction;
    String toDeleteString(K object) throws InvalidClassForPHPExtraction;
}
