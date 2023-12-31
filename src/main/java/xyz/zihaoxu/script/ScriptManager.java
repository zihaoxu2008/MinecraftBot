package xyz.zihaoxu.script;

import org.apache.commons.io.IOUtils;
import xyz.zihaoxu.Utils.Logger;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScriptManager {
    public static final File dir = new File("scripts");
    public static final File stdDir = new File(dir, "std");

    private final List<Script> scripts = new ArrayList<>();
    private Logger logger;

    public void init() throws IOException{
        this.logger=new Logger("ScriptManager");
        if (!dir.exists()) dir.mkdir();
        // if (!stdDir.exists()) stdDir.mkdir();

        // saveResource(new File(dir, "example.js"), "scripts/example.js", false);
    }

    public void loadFolder(File folder) throws ScriptException, IOException {
        for (File f: Objects.requireNonNull(folder.listFiles(((dir, name) -> name.endsWith(".js"))))) {
            logger.info("正在加载脚本: "+f.toPath());
            load(new String(IOUtils.toByteArray(Files.newInputStream(f.toPath()))));
        }
    }

    public void load(String content) throws ScriptException {
        scripts.add(new Script(content));
    }

    public void call(String name, Object... args) {
        for (Script s: scripts) {
            try {
                s.call(name, args);
            } catch (Throwable t) {
                System.out.println("处理脚本 " + s.getName() + "的事件 " + name + "时出现问题");
                t.printStackTrace();
            }
        }
    }
}
