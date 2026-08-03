package cn.academy.network;

import cn.academy.ACConfig;
import com.google.common.base.Charsets;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.io.UnsupportedEncodingException;

/**
 * Created by Paindar on 2016/8/31.
 */
public class MessageConfig implements IMessage
{
    public static class Handler implements IMessageHandler<MessageConfig, IMessage>
    {
        @Override
        public IMessage onMessage(MessageConfig message, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                ACConfig.updateConfig(message.config);
            }

            return null;
        }
    }
    public Config config;

    @Override
    public void fromBytes(ByteBuf byteBuf)
    {
        String string = byteBuf.toString(Charsets.UTF_8);
        try {
            config = ConfigFactory.parseString(string);
        } catch (Exception ex) {
            config = null;
        }
    }


    @Override
    public void toBytes(ByteBuf byteBuf)
    {
        String string=config.root().render();

        try {
            byteBuf.writeBytes(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
