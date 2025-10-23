package uk.firedev.guilds.guild.rank;

import uk.firedev.guilds.guild.Guild;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum RankType {
    OWNER(Guild::getOwnerRank),
    OFFICER(Guild::getOfficerRank),
    TREASURER(Guild::getTreasurerRank),
    RECRUITER(Guild::getRecruiterRank),
    MEMBER(Guild::getMemberRank);

    private final Function<Guild, Rank> getter;

    RankType(Function<Guild, Rank> getter) {
        this.getter = getter;
    }

    public Rank getRankInstance(Guild guild) {
        return getter.apply(guild);
    }

    public static List<RankType> getTypesAsList(boolean includeOwner) {
        if (includeOwner) {
            return Arrays.asList(values());
        }
        List<RankType> list = new ArrayList<>(List.of(values()));
        list.remove(OWNER);
        return list;
    }

}
