package de.pcvikings.discordbot.listeners;

import de.pcvikings.discordbot.DiscordBot;
import de.pcvikings.discordbot.configs.RoleConfig;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

public class ReactionListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        RestAction<Message> action = event.getChannel().retrieveMessageById(event.getMessageIdLong());
        Message message = action.complete();
        String rawMessage = message.getContentRaw();
        String[] messageArray = rawMessage.split(" ");

        if(rawMessage.charAt(0) != '!') {
            if(message.getIdLong() == RoleConfig.ROLEREACTMESSAGE) {
                if(!RoleConfig.REACTIONROLES.containsKey(event.getReactionEmote().getEmoji())) {
                    event.getReaction().removeReaction().queue();
                    return;
                }

                DiscordBot.getInstance().getCurrentGuild().addRoleToMember(event.getMember(), DiscordBot.getInstance().getCurrentGuild().getRoleById(RoleConfig.REACTIONROLES.get(event.getReactionEmote().getEmoji()))).queue();
            }

            return;
        }

        if(!(messageArray[0].equalsIgnoreCase("!team") && messageArray[1].equalsIgnoreCase("add"))) {
            return;
        }

        if(message.getMentionedMembers().size() == 0 || !message.getMentionedMembers().contains(event.getMember())) {
            return;
        }

        Member member = DiscordBot.getInstance().getCurrentGuild().retrieveMember(event.getUser()).complete();
        if(DiscordBot.getInstance().getTeamManager().getTeam(message.getAuthor().getIdLong()) != null && DiscordBot.getInstance().getTeamManager().getTeam(member.getIdLong()) == null) {
            DiscordBot.getInstance().getTeamManager().getTeam(message.getAuthor().getIdLong()).addTeamMember(member.getIdLong());
            DiscordBot.getInstance().getCurrentGuild().addRoleToMember(member, DiscordBot.getInstance().getCurrentGuild().getRoleById(DiscordBot.getInstance().getTeamManager().getTeam(message.getAuthor().getIdLong()).getRoleId())).queue();
            message.delete().queue();
        } else {
            event.getTextChannel().sendMessage("Es ist ein Fehler beim Betreten des Teams aufgetreten!");
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        RestAction<Message> action = event.getChannel().retrieveMessageById(event.getMessageIdLong());
        Message message = action.complete();
        String rawMessage = message.getContentRaw();

        if(event.getMember() == null) {
            System.out.println("Ein Fehler ist beim Entfernen einer Reaktion aufgetreten!");
            return;
        }

        Member member = DiscordBot.getInstance().getCurrentGuild().retrieveMemberById(event.getMessageIdLong()).complete();

        if(message.getIdLong() == RoleConfig.ROLEREACTMESSAGE) {
            if(!RoleConfig.REACTIONROLES.containsKey(event.getReactionEmote().getEmoji())) {
                return;
            }

            Role role = DiscordBot.getInstance().getCurrentGuild().getRoleById(RoleConfig.REACTIONROLES.get(event.getReactionEmote().getEmoji()));
            if(!member.getRoles().contains(role)) {
                return;
            }

            DiscordBot.getInstance().getCurrentGuild().removeRoleFromMember(member, role).queue();
        }
    }
}
