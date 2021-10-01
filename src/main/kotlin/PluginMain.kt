package org.example.mirai.plugin


import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.getMember
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.contact.isOwner
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MemberJoinRequestEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.info
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO


object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "org.example.sakuya",
        name = "sakuya",
        version = "0.1.0"
    )
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }

        globalEventChannel().subscribeAlways<GroupMessageEvent>{
            val path = System.getProperty("user.dir")+"\\blacklist"
            val patht = "$path\\black"
            val pathc = "$path\\admin"
            val pathi = "$path\\image"
            val bad = sender.id.toString()
            val fil = File("$path\\black\\$bad.txt")
            if (fil.exists()){
                if(bot.getGroup(sender.group.id)?.botPermission?.isOperator() == true){
                    group.getMember(sender.id)?.kick(" ")

                }
                else{
                    group.sendMessage(At(sender.id) + PlainText("此人有云黑记录,交易时请多加小心"))

                }
            }
            if (message.contentToString() == "初始化云黑"){
                val black = File(path)
                val blackt = File(patht)
                val blackc = File(pathc)
                val image = File(pathi)
                val links = File("$path\\adminlist.txt")
                if(sender.id.toString() == "2377336479"){
                if (!black.exists()) {
                    black.mkdir()
                    blackt.mkdirs()
                    blackc.mkdirs()
                    image.mkdirs()
                    links.createNewFile()
                    group.sendMessage("云黑初始化完毕")
                }
                else{group.sendMessage("不能重复初始化！")}
              }
                else{group.sendMessage("你没有权限")}
            }
            if (message.contentToString().startsWith("添加审核员")) {
                if (sender.id.toString() == "2377336479") {
                    val mes = message.contentToString()
                    val message = mes.substring(mes.indexOf(" ") + 1)
                    val file = File("$path\\admin\\$message.txt")
                    file.createNewFile()
                    val ran = File("$path\\adminlist.txt")
                    val adc = "$message\n"
                    val fw = FileWriter(ran,true)
                    fw.write(adc)
                    fw.close()
                    group.sendMessage("审核员已添加")
                } else {
                    group.sendMessage("你没有权限!")
                }
            }
            if (message.contentToString().startsWith("删除审核员")){
                if (sender.id.toString() == "2377336479") {
                    val mes = message.contentToString()
                    val message = mes.substring(mes.indexOf(" ") + 1)
                    val file = File("$path\\admin\\$message.txt")
                    System.gc()
                    file.delete()
                    val filec = File("$path\\adminlist.txt")
                    val ins = filec.inputStream().toString().replace("$message\n","")
                    val fw = FileWriter(filec)
                    fw.write(ins)
                    group.sendMessage("删除成功!")
                }else{
                    group.sendMessage("你没有权限!")
                }
            }
            if (message.contentToString().startsWith("添加云黑")){
                val s: List<String> = message.contentToString().split(" ")
                if(util(sender.id.toString())){
                    val message = s[1]
                    val bes = s[2]
                    val file = File("$path\\black\\$message.txt")
                    if (file.exists()) {
                        group.sendMessage("该云黑用户已经存在!")
                    } else {
                        file.createNewFile()
                        val per = sender.id.toString()
                        val ran = FileOutputStream(file)
                        val link = s[3]
                        val now = SimpleDateFormat("yyyy-MM-dd").format(Date()).toString()
                        val inf = "QQ:$message 在黑名单中！\n原因:$bes\n操作审核:$per\n证据链接:$link\n添加时间:$now\n——Sakuya云黑系统"
                        ran.write(inf.toByteArray())
                        group.sendMessage("成功录入云黑用户")
                        bot.getFriend(2377336479)?.sendMessage("审核员$sender 添加了云黑$message")
                    }
                }
            }
            if(message.contentToString().startsWith("查询")){
                val mes = message.contentToString()
                val mess = mes.substring(mes.indexOf(" ")+1)
                val file = File("$path\\black\\$mess.txt")
                if (file.exists()){
                    val url = URL("https://q4.qlogo.cn/g?b=qq&nk=$mess&s=100")
                    val img: BufferedImage = ImageIO.read(url)
                    ImageIO.write(img, "jpg", File("$pathi\\$mess.jpg"))
                    val imaget = File("$pathi\\$mess.jpg")
                    val imageb = imaget.toExternalResource()
                    val imagec = imageb.uploadAsImage(group)
                    val ins = file.inputStream()
                    ins.buffered(1024).reader(Charsets.UTF_8).use { reader ->
                        group.sendMessage(At(sender.id) + Image(imagec.imageId) + PlainText(reader.readText()))}
                    System.gc()
                    imaget.delete()
                }else{group.sendMessage("该用户不在黑名单中")}
            }
            if(message.contentToString().startsWith("删除")){
                val mes = message.contentToString()
                val mess = mes.substring(mes.indexOf(" ")+1)
                val file = File("$path\\black\\$mess.txt")
                if(sender.id == 2377336479){
                    if(file.exists()){
                        System.gc()
                        file.delete()
                        group.sendMessage("删除成功")
                  }else{
                      group.sendMessage("该用户不在云黑中!")}
                   }else{
                    group.sendMessage("你没有权限这样做!")
                   }
                 }
            if(message.contentToString() == "审核员列表"){
                val file = File("$path\\adminlist.txt")
                val ins = file.inputStream()
                ins.buffered(1024).reader(Charsets.UTF_8).use { reader ->
                    group.sendMessage("审核员列表:\n" + reader.readText())}
            }
            if(message.contentToString() == "T") {
                if(sender.isOwner()){
                val file = File("$path\\black")
                val test = file.list()
                    group.sendMessage("开始清除黑名单....")
                for (t in test){
                val c = t.replace(".txt","")
                    group.getMember(c.toLong())?.kick(" ")
                    group.sendMessage("黑名单成员已经全部踢出")
                }
            }else{
                group.sendMessage("你没有权限这样做!")
            }
           }
            if(message.contentToString().startsWith("关于")){
                val imaget = File("$path\\1.jpg")
                val imageb = imaget.toExternalResource()
                val imagec = imageb.uploadAsImage(group)
                group.sendMessage(PlainText("Sakuya云黑 名字来源于\n东方project中的" +
                    " 十六夜咲夜（いざよい さくや，Izayoi Sakuya)\n致力于维护圈内交易环境安全稳定\n" +
                    "官方网址:http://yunsakuya.xyz\n")+Image(imagec.imageId))
            }
            if(message.contentToString() == "转发"){
                if (sender.id == 2377336479){
                    val jk = bot.groups
                    for (i in jk){
                        bot.getGroup(i.id)?.sendMessage("官方网址:http://yunsakuya.xyz")
                        Thread.sleep(5)
                    }
                }
            }
        }
        globalEventChannel().subscribeAlways<MemberJoinRequestEvent>{
            val per =fromId.toString()
            val path = System.getProperty("user.dir")+"\\blacklist"
            val file = File("$path\\black\\$per.txt")
            if (file.exists()){
                reject(false,"你在黑名单中!")
            }
        }
        globalEventChannel().subscribeAlways<BotInvitedJoinGroupRequestEvent>{
            if(invitorId == 2377336479){
                accept()
            }
        }
     }
   }

     fun util(num: String): Boolean {
        val kite = File(System.getProperty("user.dir")+"\\blacklist"+"\\admin\\"+num+".txt")
        return kite.exists()
    }
