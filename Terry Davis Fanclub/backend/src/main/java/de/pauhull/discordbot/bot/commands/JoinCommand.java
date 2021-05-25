package de.pauhull.discordbot.bot.commands;

import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.manager.TeamManager;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;

public class JoinCommand implements Command {

    @Override
    public void execute(MessageChannel channel, Member member, Message ignored, String label, String[] args) {

        DiscordBot bot = DiscordBot.getInstance();
        String prefix = bot.getConfig().getCommands().getPrefix();

        if (args.length < 1) {
            bot.sendMessageError(channel, "Verwendung: " + prefix + getLabel() + " <Teamname>");
            return;
        }

        if (member == null) {
            bot.sendMessageError(channel, bot.getConfig().getMessages().getNoPrivateMessage());
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(arg);
        }

        TeamManager.Team team = bot.getTeamManager().getTeam(builder.toString());
        if (team == null) {
            bot.sendMessageError(channel, bot.getConfig().getMessages().getTeamDoesntExist());
            return;
        }

        if (team.getOwner() != null) {

            Member owner = null;
            for (Member possibleOwner : member.getGuild().block().getMembers().toIterable()) {
                if (possibleOwner.getId().asLong() == team.getOwner()) {
                    owner = possibleOwner;
                    break;
                }
            }

            if (owner == null || member.equals(owner)) {
                bot.sendMessageError(channel, bot.getConfig().getMessages().getNoTeamJoin());
                return;
            }

            String messageContent = String.format(bot.getConfig().getMessages().getTeamJoinRequest(),
                    team.getName(), owner.getNicknameMention());
            Message message = bot.sendMessageSuccess(channel, messageContent);
            message.addReaction(ReactionEmoji.unicode("\uD83D\uDC4D")).block();
            message.addReaction(ReactionEmoji.unicode("❌")).block();

            bot.getTeamManager().getJoinRequests().stream()
                    .filter(joinRequest -> joinRequest.getMember().equals(member))
                    .forEach(joinRequest -> joinRequest.getMessage().delete().block());
            bot.getTeamManager().getJoinRequests()
                    .removeIf(joinRequest -> joinRequest.getMember().equals(member));
            bot.getTeamManager().getJoinRequests()
                    .add(new TeamManager.JoinRequest(message, member, owner, team));
            return;
        }

        bot.getTeamManager().assignTeam(member, team);
        bot.sendMessageSuccess(channel, String.format(bot.getConfig().getMessages().getTeamAssigned(), team.getName()));
    }

    @Override
    public String getLabel() {
        return DiscordBot.getInstance().getConfig().getCommands().getJoin();
    }

    @Override
    public String getDesc() {
        return DiscordBot.getInstance().getConfig().getCommands().getJoinDesc();
    }
}
