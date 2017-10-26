package interceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

import java.util.Map;

/**
 * Created by 林中漫步 on 2017/10/25.
 */
public class MyInterceptor extends MethodFilterInterceptor {
    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        Map<String,Object> session=invocation.getInvocationContext().getSession();
        Object name=session.get("name");
        if(name==null)
        {
            return Action.INPUT;
        }else
        {
            invocation.invoke();
        }
        return  null;
    }
}
