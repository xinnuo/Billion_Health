package com.ruanmeng;

import java.util.List;

import io.rong.callkit.AudioPlugin;
import io.rong.callkit.VideoPlugin;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.widget.provider.FilePlugin;
import io.rong.imlib.model.Conversation;

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-21 16:40
 */
public class CustomExtensionModule extends DefaultExtensionModule {

    /**
     * 返回需要展示的 plugin 列表
     */
    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
        if (conversationType.equals(Conversation.ConversationType.PRIVATE)) {
            List<IPluginModule> pluginModuleList = super.getPluginModules(conversationType);

            int audioPos = -1, videoPos = -1, filePos = -1;
            for (IPluginModule item : pluginModuleList) {
                if (item instanceof AudioPlugin) audioPos = pluginModuleList.indexOf(item);
            }
            if (audioPos > -1) pluginModuleList.remove(audioPos);

            for (IPluginModule item : pluginModuleList) {
                if (item instanceof VideoPlugin) videoPos = pluginModuleList.indexOf(item);
            }
            if (videoPos > -1) pluginModuleList.remove(videoPos);

            for (IPluginModule item : pluginModuleList) {
                if (item instanceof FilePlugin) filePos = pluginModuleList.indexOf(item);
            }
            if (filePos > -1) pluginModuleList.remove(filePos);

            return pluginModuleList;
        }
        return super.getPluginModules(conversationType);
    }

    /**
     * 返回需要展示的 EmoticonTab 列表
     */
    @Override
    public List<IEmoticonTab> getEmoticonTabs() {
        return super.getEmoticonTabs();
    }

}
