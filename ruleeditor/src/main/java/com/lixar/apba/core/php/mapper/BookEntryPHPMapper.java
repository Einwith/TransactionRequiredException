package com.lixar.apba.core.php.mapper;

import com.lixar.apba.core.php.PHPMap;
import com.lixar.apba.core.php.PHPStringBuilder;
import com.lixar.apba.core.php.errors.InvalidClassForPHPExtraction;
import com.lixar.apba.domain.BookEntry;
import org.apache.commons.lang.NotImplementedException;

public class BookEntryPHPMapper implements PHPMap<BookEntry> {
    @Override
    public String toInsertString(BookEntry book) throws InvalidClassForPHPExtraction {
        PHPStringBuilder sb = new PHPStringBuilder();

        sb.appendRawLine("$bookentry = new BookEntry();");
        sb.appendRaw("$bookentry->createNewEntry(").appendValue(book.getPage()).appendRaw(", ");
        sb.appendValue(book.getSection()).appendRaw(", ");
        sb.appendValue(book.getResultId()).appendRaw(", ");
        sb.appendValue(book.getResultBook()).appendRaw(", ");
        sb.appendValue(book.getResultCurrent()).appendRaw(", ");
        sb.appendValue(book.getLocal()).appendRaw(", ");
        sb.appendValue(book.getTextCondition()).appendRawLine(");");

        sb.appendPersistAndFlush("$bookentry");

        return sb.toString();
    }

    @Override
    public String toUpdateString(BookEntry book, String[] updateFields) throws InvalidClassForPHPExtraction {
        PHPStringBuilder sb = new PHPStringBuilder();
        appendFind(sb, book);

        for (String updateField : updateFields) {
            switch (updateField) {
                case "resultCurrent":
                    sb.appendRaw("$bookentry->setResultCurrent(").appendValue(book.getResultCurrent()).appendRawLine(");");
                    break;
                default:
                    throw new NotImplementedException();
            }
        }
        sb.appendPersistAndFlush("$bookentry");

        return sb.toString();
    }

    @Override
    public String toDeleteString(BookEntry book) throws InvalidClassForPHPExtraction {
        PHPStringBuilder sb = new PHPStringBuilder();
        appendFind(sb, book);
        sb.appendRawLine("$manager->remove($bookentry);");
        sb.appendRawLine("$manager->flush();");

        return sb.toString();
    }

    private void appendFind(PHPStringBuilder sb, BookEntry book) {
        sb.appendRaw("$bookentry = $doctrine->getRepository('StratdgiBookBundle:BookEntry')->findOneBy(array('page'=>").appendValue(book.getPage()).appendRaw(", ");
        sb.appendRaw("'section'=>").appendValue(book.getSection()).appendRaw(", ");
        sb.appendRaw("'result_id'=>").appendValue(book.getResultId()).appendRaw(", ");
        sb.appendRaw("'result_book'=>").appendValue(book.getResultBook()).appendRaw(", ");
        sb.appendRaw("'text_condition'=>").appendValue(book.getTextCondition()).appendRawLine("));");
    }
}
