# RobolectricDemo
## 项目介绍
一个使用Robolectric完成所有单元测试的项目，企业级脱敏开源项目，供同样搞单测的小伙伴参考。

## 前言
作者本人目前在一家车载企业负责基础项目的搭建，最近负责搭建所在企业的单元测试框架。
一开始尝试
发现虽然网上单元测试的项目或者文章众多，但是很少又可用的，其原因，就是因为项目结构偏简单无法支撑起企业级项目复杂的结果，或者相关的文章太老，已经失去了参考的意义。



# 一.单元测试介绍
目前，跑在JVM虚拟机上的单测方案主要是Robolectric，而且这也是google官方推荐的一种单元测试的方案。  
单元测试是针对单个方法或类的，自然要排除掉相关的依赖，因此mock工具也是必不可少的。 mock的方案有很多面，主要有以下两种：  
mockito：基于cglib的动态代理实现的，可以mock掉替换掉类中的方法，构造方法，静态方法。  
powermock：mockito的增强版本，基于JDK动态代理的方式，所以可以实现任何的替换。但是缺点是需要替换原有的运行注解，会导致跑单测有问题，而且很久不维护了。  
作者在实际使用中，发现powermock会对Robolectric的单测运行产生一些影响，并且由于mockito也已经支持了静态方法的mock，所以，最终放弃使用powermock。

# 二.单测规范
采用类对类，方法对方法的规范。  
1.如果需要对MainActivity写单元测试，则需要在test文件夹下，相同包名，创建MainActivityTest的单元测试类。  
单元测试类的类名为：原类名+Test。  
![整体效果图](img/unit_test_p1.png)

2.方法对方法。针对某个方法写代码单测，推荐使用一对一的场景。  
比如针对updateErrorIv方法写单元测试，则单元测试的方法为：testIvErrorState()  
当然，涉及到某些具体相关的业务，会出现多个方法对应一个单元测试方法的情况，这种情况也是完全可以的。

# 三.配置流程
## 1.gradle版本
gradle版本并不是强制要求，只是方便读者更方便的运行本项目。
gradle-wrapper.properties中配置gradle-6.7.1-all版本。   
`distributionUrl=https\://services.gradle.org/distributions/gradle-6.7.1-all.zip`
项目中build.gradle配置版本如下：
```
buildscript {
    dependencies {
        classpath "com.android.tools.build:gradle:4.2.2"
    }
}
```
## 2.依赖配置
app目录下的build.gradle下，配置如下依赖：
```
    testImplementation('junit:junit:4.13.2') {
        exclude group: 'org.hamcrest', module: 'hamcrest-core'
    }
    testImplementation "io.mockk:mockk:1.12.2"
    testImplementation "org.assertj:assertj-core:3.22.0"
    testImplementation "org.robolectric:robolectric:4.9.2"
    testImplementation('org.mockito:mockito-core:3.6.28') {
        exclude group: 'net.bytebuddy', module: 'byte-buddy'
        exclude group: 'net.bytebuddy', module: 'byte-buddy-agent'
    }
    testImplementation 'org.mockito:mockito-inline:5.2.0'
    testImplementation "androidx.test:core:1.3.0"

    testImplementation("org.hamcrest:hamcrest-core:1.3")
    testImplementation("org.assertj:assertj-core:2.6.0")
    testImplementation 'android.arch.core:core-testing:1.0.0-alpha3'
```

## 3.创建单元测试类
创建单元测试类，@Before代表执行前的初始化操作。@Test代表执行单元测试操作。
```
@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {
    @Before
    public void init() {
        APIMock.isUnitTest = true;
    }
    
    @Test
    public void testAny() {
    
    }
}
```

# 四.单测样例介绍
这里，我们以MVPActivity界面为例介绍。  
首先，我们介绍下MVPActivity界面的功能：  
1.进入页面，注册自定义监听，退出页面，取消注册自定义监听。  
2.首次进入或者回到页面的时候，请求数据并刷新页面，并且只请求一次。
3.展示fragment。
4.点击图标，会弹出dialog。

详细代码参见：
[MVPActivity.java](https://github.com/aa5279aa/RobolectricDemo/blob/main/app/src/main/java/com/xt/robolectricdemo/mvp/MVPActivity.java)



梳理代码逻辑：
1.init()方法，里面主要做了5个逻辑。检查权限/调用presenter.getHomeInfo()方法/展示OrderListNewFragment页面/注册监听/更新图标。
2.onResume()方法，首次进入，不执行reloadData()，而后续调用onResume，则执行reloadData()。  
3.getInfoAgain()方法，如果presenter为空，则不执行，不为空，则执行presenter.getHomeInfo()。  
4.initDataListener()方法，进入页面时注册监听。  
5.onDestroy方法，退出时取消注册监听。  
6.testUpdateErrorIv()方法，根据状态显示不同的图标。

汇总整理如下：
![整体效果图](img/unit_test_p2.png)



# 五.常见问题
## 5.1常见问题汇总
### 1.类中引用SO问题
因为单元测试基于JVM虚拟机，执行的是java的流程，并不会执行相关的安卓打包流程，所以引用的SO文件不会打包进最终产物当中，因此，直接运行项目，会提示SO找不到。
所以，更合适的方式是把相关的类进行mock替换掉。

如果遇到业务使用到了SO文件，则需要进行mock。
比如我们的BeanOKHttp中，就使用到了so库，所以，我们需要在上层就mock掉，使其不执行相关的请求和初始化操作。
下面的代码，是来确保执行到CarservicesSpaceModule.getInstance().getEtcAccountInfoWithCacheC03()方法时，不去真的执行这个方法，而是直接返回noNetWorkFlowable对象。
```
@Test
public void testIvErrorState() {
    Flowable<BaseEntity<EtcAccountInfoEntity>> noNetWorkFlowable = Flowable.create(emitter -> {
        BaseEntity<EtcAccountInfoEntity> baseEntity = new BaseEntity<>();
        EtcAccountInfoEntity accountInfoEntity = new EtcAccountInfoEntity();
        accountInfoEntity.setStatus(CarServiceConstant.EtcStatus.ACCOUNT_BLACKLIST);
        baseEntity.setCode("400");
        baseEntity.setDescription("fail");
        baseEntity.setData(accountInfoEntity);
        emitter.onNext(baseEntity);
    }, BackpressureStrategy.BUFFER);
    CarservicesSpaceModule mock = mock(CarservicesSpaceModule.class);
    try (MockedStatic<CarservicesSpaceModule> ignored = mockStatic(CarservicesSpaceModule.class)) {
    when(CarservicesSpaceModule.getInstance()).thenReturn(mock);
    when(CarservicesSpaceModule.getInstance().getEtcAccountInfoWithCacheC03()).thenReturn(noNetWorkFlowable);
    //后面进行验证操作
 }
```
### 2.解决XML中View类引用SO问题
如果XML引用了某个View的类，并且这个类引用了SO，则需要整体MOCK，并且这种mock，是无法通过依赖去解决的。
比如：SoView中使用到了SO文件。
则会产生如下报错，因为
```
Caused by: java.lang.UnsatisfiedLinkError: no Java2C in java.library.path: [/Users/liuxl1/Library/Java/Extensions, /Library/Java/Extensions, /Network/Library/Java/Extensions, /System/Library/Java/Extensions, /usr/lib/java, .]
	at java.base/java.lang.ClassLoader.loadLibrary(ClassLoader.java:2670)
```


## 5.2 常见排查手段
1.使用GPT  
2.百度/google
3.参照网上现有的项目，比如：https://github.com/ankidroid/Anki-Android



# 六.参考文档
| 地址                                                                              | 介绍                                                            |
|---------------------------------------------------------------------------------|---------------------------------------------------------------|
| https://github.com/ankidroid/Anki-Android                                       | github上单测覆盖率最高的项目                                             |
| https://github.com/mockito/mockito                                              | github上mockito项目                                              |
| https://github.com/robolectric/robolectric                                      | github上robolectric项目                                          |
| https://github.com/powermock/powermock                                          | github上powermock项目                                            |
| https://developer.android.com/reference/androidx/test/core/app/ActivityScenario | 官方关于ActivityScenario的介绍ActivityScenario用于替代ActivityController |


