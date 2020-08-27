# PatternScanner

### 适配
扫描指令修改==PatternScan.Tasks.ScanSameIDTask==文件, 目前只适配了两种指令格式, 如果本地测试没有扫描到的话, 可以单步调试适配一下对应的指令格式.

### 使用

```shell
java -jar ./PatternScanner.jar -j 6 -d /home/zhangz/annoymous/findTargetPattern/category_top5000.json -r ./
```
-j 线程数
-d applist路径
-r 指定输出路径, 暂时没用, 可以输出重定向替代..