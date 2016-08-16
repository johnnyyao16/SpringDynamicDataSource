#使用Spring Dynamic DataSource来实现读写分离


Spring有AbstractRoutingDataSource，可以在运行时动态切换数据源。根据读写分离的策略，我们在service的方法上加上DataSource注解，指明使用哪个数据源。

##实现动态数据源
1. 创建DynamicDataSource，继承自AbstractRoutingDataSource，需要实现determineCurrentLookupKey()方法。Spring会在事物开启时，根据这个方法查找使用哪个数据源

		public class DynamicDataSource extends AbstractRoutingDataSource {

    		@Override
    		protected Object determineCurrentLookupKey() {
        		return DynamicDataSourceHolder.getDataSouce();
    		}
   
		}
2. 创建DynamicDataSourceHolder，我们定义了一个ThreadLocal级别变量，目的是支持并发多线程，并把当前数据源作为动态变量。
		
		public class DynamicDataSourceHolder {
    		public static final ThreadLocal<String> holder = new ThreadLocal<String>();
    
    		public static void putDataSource(String name) {
        		holder.set(name);
    		}
    
    		public static String getDataSouce() {
        		return holder.get();
    		}
		}
3. 定义DataSource annotation
		
		@Retention(RetentionPolicy.RUNTIME)
		@Target({ElementType.METHOD})
		public @interface DataSource {
    		String value();
		}
		
4. Service里使用annotation，指明使用哪个数据源
	
		@Override
    	@DataSource("master")
    	@Transactional
    	public User getUserFromMaster(String userId) {
       		return userMapper.selectUser(userId);
    	}
    	
5. 创建aop切面，在service方法被调用前，设置数据源。当service方法被调用的时候，更加方法上的DataSource注解，把DataSource的key设置到DynamicDataSourceHolder上。

		public class DataSourceAspect {
    
    		public void before(JoinPoint point) {
        		Object target = point.getTarget();
        		String method = point.getSignature().getName();
        
        		Class<?> clazz = target.getClass();
        
        		Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
        		try {
            		Method m = clazz.getMethod(method, parameterTypes);
            		if (m != null && m.isAnnotationPresent(DataSource.class)) {
                		DataSource data = m.getAnnotation(DataSource.class);
                		DynamicDataSourceHolder.putDataSource(data.value());
            		}
            
        		} catch (Exception e) {
            		// TODO: handle exception
        		}
    		}
    		
6. 配置xml

		<bean id="masterDataSource" class="com.alibaba.druid.pool.DruidDataSource"
			init-method="init" destroy-method="close">
			<property name="driverClassName" value="org.postgresql.Driver" />
			<property name="url" value="jdbc:postgresql://localhost:5432/postgres" />
			<property name="username" value="maycuruser" />
			<property name="password" value="Java01!!" />
		</bean>

		<bean id="slaveDataSource" class="com.alibaba.druid.pool.DruidDataSource"
			init-method="init" destroy-method="close">
			<property name="driverClassName" value="org.postgresql.Driver" />
			<property name="url" value="jdbc:postgresql://localhost:5432/yaoyufan" />
			<property name="username" value="maycuruser" />
			<property name="password" value="Java01!!" />
		</bean>

		<bean id="dataSource" class="com.johnny.test.component.DynamicDataSource">
			<property name="targetDataSources">
				<map key-type="java.lang.String">
					<!-- write -->
					<entry key="master" value-ref="masterDataSource" />
					<!-- read -->
					<entry key="slave" value-ref="slaveDataSource" />
				</map>

			</property>
			<property name="defaultTargetDataSource" ref="masterDataSource" />
		</bean>
		
		<aop:aspectj-autoproxy></aop:aspectj-autoproxy>
		<bean id="dataSourceAspect" class="com.johnny.test.aspect.DataSourceAspect" />
		<aop:config>
			<aop:aspect id="dataAspect" ref="dataSourceAspect" order="1">
				<aop:pointcut id="aspectPoint"
					expression="execution(* com.johnny.test.service.*.*(..))" />
				<aop:before pointcut-ref="aspectPoint" method="before" />
			</aop:aspect>
		</aop:config>