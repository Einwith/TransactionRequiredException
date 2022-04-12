package com.lixar.apba.core.php.mapper;

import com.lixar.apba.core.php.PHPMap;
import com.lixar.apba.core.php.PHPStringBuilder;
import com.lixar.apba.core.php.errors.InvalidClassForPHPExtraction;
import com.lixar.apba.domain.DiceResult;
import org.apache.commons.lang.NotImplementedException;

public class DiceResultPHPMapper implements PHPMap<DiceResult> {

    @Override
    public String toInsertString(DiceResult object) throws InvalidClassForPHPExtraction {
        throw new NotImplementedException();
    }

    @Override
    public String toUpdateString(DiceResult diceResult, String[] updateFields) throws InvalidClassForPHPExtraction {

        PHPStringBuilder sb = new PHPStringBuilder();
        appendFind(sb, diceResult);

        for (String updateField : updateFields) {
            switch (updateField) {
                case "resultParser":
                    sb.appendRaw("$diceresult->setResultParser(").appendValue(diceResult.getResultParser()).appendRawLine(");");
                    break;
                case "resultConsole":
                    sb.appendRaw("$diceresult->setResultConsole(").appendValue(diceResult.getResultConsole()).appendRawLine(");");
                    break;
                default:
                    throw new NotImplementedException();
            }
        }
        sb.appendPersistAndFlush("$diceresult");

        return sb.toString();
    }

    @Override
    public String toDeleteString(DiceResult object) throws InvalidClassForPHPExtraction {
        throw new NotImplementedException();
    }

    private void appendFind(PHPStringBuilder sb, DiceResult diceResult) {
        sb.appendRaw("$diceresult = $doctrine->getRepository('StratdgiEngineBundle:DiceResult')->findOneBy(array('dice_pool'=>").appendValue(diceResult.getDicePool()).appendRaw(", ");
        sb.appendRaw("'dice_sid'=>").appendValue(diceResult.getDiceSid()).appendRaw(", ");
        sb.appendRaw("'requirement'=>").appendValue(diceResult.getRequirement()).appendRawLine("));");
    }
}
