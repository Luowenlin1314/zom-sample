mybatis代码生成插件使用说明
1. 修改generatorConfig.properties中的 数据库驱动jar路径、数据库连接参数、包路径配置
2. 修改generatorConfig.xml中的<table>，需要生成哪张表就指定相应的名称，并配置需要的生成的查询方法
3. Eclipse：
   选中工程，右键Run As-->Maven Build…-->在Goals框中输入：mybatis-generator:generate
   IDEA：
   点击MavenProject标签,选中skyids/Plugins/mybatis-generator/mybatis-generator:generate，点击Run按钮即可生产代码