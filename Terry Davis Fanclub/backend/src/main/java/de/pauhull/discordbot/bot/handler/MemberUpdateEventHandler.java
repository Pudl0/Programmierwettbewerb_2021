package de.pauhull.discordbot.bot.handler;

import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.manager.TeamManager;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.MemberUpdateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MemberUpdateEventHandler implements Consumer<MemberUpdateEvent> {

    @Override
    public void accept(MemberUpdateEvent event) {

        DiscordBot bot = DiscordBot.getInstance();
        Member old = event.getOld().orElse(null);
        if (old == null) {
            return;
        }

        if (event.getCurrentRoles().equals(old.getRoleIds())) {
            return;
        }

        Guild guild = event.getGuild().block();
        if (guild == null) {
            return;
        }

        Set<Snowflake> rolesGained = event.getCurrentRoles();
        rolesGained.removeAll(old.getRoleIds());
        Set<Snowflake> rolesRemoved = old.getRoleIds();
        rolesRemoved.removeAll(event.getCurrentRoles());

        BiConsumer<Collection<Snowflake>, Consumer<TeamManager.Team>> apply = (collection, consumer) ->
                collection.stream()
                        .map(snowflake -> Optional.ofNullable(guild.getRoleById(snowflake).block()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(role -> Optional.ofNullable(bot.getTeamManager().getTeam(role.getName())))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(consumer);

        apply.accept(rolesGained, team -> {
            team.getMembers().add(event.getMemberId().asString());
            bot.getTeamManager().save();
        });

        apply.accept(rolesRemoved, team -> {
            team.getMembers().remove(event.getMemberId().asString());
            bot.getTeamManager().save();
        });
    }
}
