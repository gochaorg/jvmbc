package xyz.cofe.jvmbc.anlz;

import xyz.cofe.jvmbc.cls.CMethod;
import xyz.cofe.jvmbc.fn.Either;
import static xyz.cofe.jvmbc.fn.Either.left;
import static xyz.cofe.jvmbc.fn.Either.right;
import xyz.cofe.jvmbc.fn.T2;
import xyz.cofe.jvmbc.mth.MJump;
import xyz.cofe.jvmbc.mth.MLabel;
import xyz.cofe.jvmbc.mth.MLineNumber;
import xyz.cofe.jvmbc.mth.MLocalVariable;
import xyz.cofe.jvmbc.mth.MLocalVariableAnnotation;
import xyz.cofe.jvmbc.mth.MLookupSwitch;
import xyz.cofe.jvmbc.mth.MTableSwitch;
import xyz.cofe.jvmbc.mth.MTryCatchBlock;
import xyz.cofe.jvmbc.mth.MethodByteCode;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Проверка ссылок, что ссылки указываю на целевые метки
 *
 * {@link xyz.cofe.jvmbc.mth.MTryCatchBlock}
 * {@link xyz.cofe.jvmbc.mth.MTableSwitch}
 * {@link xyz.cofe.jvmbc.mth.MLookupSwitch}
 * {@link xyz.cofe.jvmbc.mth.MLocalVariableAnnotation}
 * {@link xyz.cofe.jvmbc.mth.MLocalVariable}
 * {@link xyz.cofe.jvmbc.mth.MLineNumber}
 * {@link xyz.cofe.jvmbc.mth.MJump}
 */
public class LabelRefChecker
{
    private List<MethodByteCode> body;

    public LabelRefChecker( List<MethodByteCode> body ){
        if( body==null )throw new IllegalArgumentException( "body==null" );
        this.body = body;
    }

    public LabelRefChecker( CMethod<?> method ){
        if( method==null )throw new IllegalArgumentException( "method==null" );
        this.body = method.getMethodByteCodes();
    }

    public static class AllMLabels {
        public final Set<MLabel> unnamedLabels;
        public final Map<String,Set<MLabel>> labelsByName;
        public final Map<String,Set<MLabel>> duplicates;
        public final Map<String,MLabel> uniques;

        public AllMLabels( Set<MLabel> unnamedLabels,
                           Map<String, Set<MLabel>> labelsByName,
                           Map<String, Set<MLabel>> duplicates,
                           Map<String, MLabel> uniques
        ){
            this.unnamedLabels = unnamedLabels;
            this.labelsByName = labelsByName;
            this.duplicates = duplicates;
            this.uniques = uniques;
        }
    }
    public AllMLabels allMLabels(){
        var unnamedLabels = new LinkedHashSet<MLabel>();
        var labelsByName = new LinkedHashMap<String, Set<MLabel>>();
        for( var ins : body ){
            if( ins instanceof MLabel ){
                var mlabel = (MLabel)ins;
                var name = mlabel.getName();
                if( name==null ){
                    unnamedLabels.add(mlabel);
                }else{
                    labelsByName.computeIfAbsent(name, x -> new LinkedHashSet<>()).add(mlabel);
                }
            }
        }
        var duplicates = labelsByName
            .entrySet().stream().filter(e -> e.getValue().size()>1)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        var uniques = labelsByName
            .entrySet().stream().filter(e -> e.getValue().size()==1)
            .collect(Collectors.toMap(Map.Entry::getKey, v->v.getValue().iterator().next()));

        return new AllMLabels(
            unnamedLabels,
            labelsByName,
            duplicates,
            uniques
        );
    }

    private AllMLabels allMLabels_value;
    public AllMLabels getAllMLabels(){
        if( allMLabels_value!=null )return allMLabels_value;
        allMLabels_value = allMLabels();
        return allMLabels_value;
    }

    private T2<MethodByteCode, Either<String,Set<MLabel>>> checkRef( MethodByteCode bc, AllMLabels allLabels ){
        if( bc instanceof MTryCatchBlock ){
            return checkRef((MTryCatchBlock)bc, allLabels);
        }else if( bc instanceof MTableSwitch ){
            checkRef((MTableSwitch)bc, allLabels);
        }else if( bc instanceof MLookupSwitch ){
            checkRef((MLookupSwitch)bc, allLabels);
        }else if( bc instanceof MLocalVariableAnnotation ){
            checkRef((MLocalVariableAnnotation)bc, allLabels);
        }else if( bc instanceof MLocalVariable ){
            return checkRef((MLocalVariable)bc, allLabels);
        }else if( bc instanceof MLineNumber ){
            return checkRef((MLineNumber)bc, allLabels);
        }else if( bc instanceof MJump ){
            return checkRef((MJump)bc, allLabels);
        }
        return T2.of(bc,right(Set.of()));
    }

    private T2<MethodByteCode, Either<String,Set<MLabel>>> checkRef( MethodByteCode bc, AllMLabels allMLabels, Map<String,String> refz ){
        var err = new StringBuilder();
        var refs = new LinkedHashSet<MLabel>();
        refz.forEach((name,lbl) -> {
            if( lbl==null ){
                err.append(name).append(" is null\n");
            }else {
                var startTo = allMLabels.uniques.get(lbl);
                if( startTo==null ){
                    err.append(name).append("=\"").append(lbl).append("\" is miss\n");
                }else{
                    refs.add(startTo);
                }
            }
        });
        if( err.length()>0 )return T2.of(bc, left(err.toString()));
        return T2.of(bc,right(refs));
    }
    private T2<MethodByteCode, Either<String,Set<MLabel>>> checkRef( MTryCatchBlock bc, AllMLabels allMLabels ){
        return checkRef(
            bc,
            allMLabels,
            Map.of(
            "start", bc.getLabelStart(),
            "end", bc.getLabelEnd(),
            "handler", bc.getLabelHandler()
            )
        );
    }

    private void checkRef( MTableSwitch bc, AllMLabels allMLabels ){}
    private void checkRef( MLookupSwitch bc, AllMLabels allMLabels ){}
    private void checkRef( MLocalVariableAnnotation bc, AllMLabels allMLabels ){}
    private T2<MethodByteCode, Either<String,Set<MLabel>>> checkRef( MLocalVariable bc, AllMLabels allMLabels ){
        return checkRef(
            bc,
            allMLabels,
            Map.of(
                "labelStart", bc.getLabelStart(),
                "labelEnd", bc.getLabelEnd()
            )
        );
    }
    private T2<MethodByteCode, Either<String,Set<MLabel>>> checkRef( MLineNumber bc, AllMLabels allMLabels ){
        return checkRef(
            bc,
            allMLabels,
            Map.of(
                "label", bc.getLabel()
            )
        );
    }
    private T2<MethodByteCode, Either<String,Set<MLabel>>> checkRef( MJump bc, AllMLabels allMLabels ){
        return checkRef(
            bc,
            allMLabels,
            Map.of(
                "label", bc.getLabel()
            )
        );
    }
}
