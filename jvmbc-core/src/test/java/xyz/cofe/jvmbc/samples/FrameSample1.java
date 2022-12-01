package xyz.cofe.jvmbc.samples;

import java.util.List;

public class FrameSample1 {
    public List sample(List<String> list){
        List lst1;
        int var2;
        int cnt = 3;
        for( int i=0;i<list.size();i++ ){
            cnt++;
            if( cnt>5 )var2 = cnt/2;
            if( cnt==10 ) {
                lst1 = list;
            }
        }

        return list;
    }
}
