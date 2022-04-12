package com.lixar.apba.core.php.mapper;

import com.lixar.apba.core.php.PHPMap;
import com.lixar.apba.core.php.PHPStringBuilder;
import com.lixar.apba.core.php.errors.InvalidClassForPHPExtraction;
import com.lixar.apba.domain.BookEntryHistory;
import org.apache.commons.lang.NotImplementedException;

public class BookEntryHistoryPHPMapper implements PHPMap<BookEntryHistory> {
    @Override
    public String toInsertString(BookEntryHistory history) throws InvalidClassForPHPExtraction {
        PHPStringBuilder sb = new PHPStringBuilder();

        sb.appendRaw("$bookentry = $doctrine->getRepository('StratdgiBookBundle:BookEntry')->findOneBy(array('id'=>" + history.getId() + "));").appendNewLine();
        sb.appendRawLine("if ($bookentry != null) {");
        sb.appendRawLine("   $history = new BookEntryHistory();");
        sb.appendRaw("   $history->setResultId($bookentry);").appendNewLine();
        sb.appendRaw("   $history->setResultTime(").appendValue(history.getResultTime()).appendRawLine(");");
        sb.appendRaw("   $history->setResultOld(").appendValue(history.getResultOld()).appendRawLine(");");
        sb.appendRaw("   $history->setConditionOld(").appendValue(history.getConditionOld()).appendRawLine(");");
        sb.appendRaw("   $history->setActionOld(").appendValue(history.getActionOld()).appendRawLine(");");

        sb.appendPersistAndFlush("$history");
        sb.appendRawLine("}");

        return sb.toString();
    }

    @Override
    public String toUpdateString(BookEntryHistory object, String[] updateFields) throws InvalidClassForPHPExtraction {
        throw new NotImplementedException();
    }

    @Override
    public String toDeleteString(BookEntryHistory object) throws InvalidClassForPHPExtraction {
        throw new NotImplementedException();
    }
}
