package net.trendl.procrasthelpercore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import net.homecredit.innovations.mongorepository.CommonMongoRepository;
import net.trendl.procrasthelpercore.domain.AppliedReward;
import net.trendl.procrasthelpercore.domain.Reward;
import org.bson.Document;
import org.joda.time.DateTime;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Tomas.Rendl on 22.7.2015.
 */
public class RewardService {

    Random rand = new Random(DateTime.now().getMillis());

    @Resource(name = "rewardRepository")
    CommonMongoRepository rewardRepository;

    @Resource(name = "appliedRewardRepository")
    CommonMongoRepository appliedRewardRepository;

    public Collection<Reward> list() {
        try {
            Collection rewardDocuments = rewardRepository.find(null);
            Collection<Reward> rewardResult = new ArrayList<Reward>();
            for (Object d : rewardDocuments) {
                rewardResult.add(convert((Document) d));
            }
            return rewardResult;
        } finally {
            rewardRepository.close();
        }
    }

    public Reward findOne(String id) {
        Document rewardObject = (Document) rewardRepository.findOne(id);
        Reward r = convert(rewardObject);
        return r;
    }

    private Reward convert(Document rewardDocument) {
        Reward r = new Reward();
        r.setId(rewardDocument.getString("id"));
        r.setName(rewardDocument.getString("name"));
        r.setAppliedDifficulty(rewardDocument.getInteger("appliedDifficulty"));
        r.setMinRepeatInterval(rewardDocument.getInteger("minRepeatInterval"));
        return r;
    }

    private AppliedReward convertAppliedReward(Document appliedRewardDocument) {
        AppliedReward appliedReward = new AppliedReward();
        appliedReward.setId(appliedRewardDocument.getString("_id"));
        appliedReward.setPending(appliedRewardDocument.getBoolean("pending"));
        Document rewardDoc = (Document)appliedRewardDocument.get("reward");
        Reward reward = convert(rewardDoc);
        appliedReward.setReward(reward);
        Document appliedDate = (Document) appliedRewardDocument.get("appliedDate");
        if (appliedDate != null) {
            DateTime dateTime = new DateTime(appliedDate.getLong("millis"));
            appliedReward.setAppliedDate(dateTime);
        }
        return appliedReward;
    }

    public void save(Reward reward) throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(reward);
        try {
            rewardRepository.save(reward.getId(), json);
        } finally {
            rewardRepository.close();
        }
    }

    public void save(AppliedReward appliedReward) throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(appliedReward);
        try {
            appliedRewardRepository.save(appliedReward.getId(), json);
        } finally {
            appliedRewardRepository.close();
        }
    }

    public int applyReward(int difficulty) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appliedDifficulty", difficulty);
        try {
            Collection foundRewardDocs = rewardRepository.find(params);
            ArrayList<Reward> applicableRewards = new ArrayList<Reward>();
            ArrayList<Reward> rewardsWithinMinimalPeriod = new ArrayList<Reward>();
            for (Object d : foundRewardDocs) {
                Reward reward = convert((Document) d);
                // find last applied reward
                HashMap<String, Object> appliedRewardParams  = new HashMap<String, Object>();
                appliedRewardParams.put("reward.id", reward.getId());
                Collection appliedRewards = appliedRewardRepository.find(appliedRewardParams, "appliedDate.millis", -1);
                if (appliedRewards.size() > 0) {
                    AppliedReward lastAppliedReward = convertAppliedReward ((Document)appliedRewards.iterator().next());
                    if (lastAppliedReward.getAppliedDate() == null ||
                            lastAppliedReward.getAppliedDate().plus(reward.getMinRepeatInterval() * 60 * 1000).isBeforeNow()) {
                        applicableRewards.add(reward);;
                    } else {
                        rewardsWithinMinimalPeriod.add(reward);
                    }
                } else {
                    applicableRewards.add(reward);
                }
            }
            // select one rewards randomly
            AppliedReward appliedReward = null;
            if (applicableRewards.size() > 0) {
                int randomIndex = rand.nextInt(applicableRewards.size());
                appliedReward = new AppliedReward();
                appliedReward.setAppliedDate(DateTime.now());
                appliedReward.setPending(true);
                appliedReward.setReward(applicableRewards.get(randomIndex));

            } else if (rewardsWithinMinimalPeriod.size() > 0) {
                int randomIndex = rand.nextInt(rewardsWithinMinimalPeriod.size());
                appliedReward = new AppliedReward();
                appliedReward.setAppliedDate(DateTime.now());
                appliedReward.setPending(true);
                appliedReward.setReward(rewardsWithinMinimalPeriod.get(randomIndex));
            }

            if (appliedReward != null) {
                appliedReward.setId(UUID.randomUUID().toString());
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = ow.writeValueAsString(appliedReward);

                appliedRewardRepository.save(appliedReward.getId(), json);
                return 1;
            }

            return 0;
        } finally {
            rewardRepository.close();
            appliedRewardRepository.close();
        }
    }

    public void deleteOne(String id) {
        try {
            rewardRepository.deleteOne(id);
        } finally {
            rewardRepository.close();
        }
    }

    public Collection<AppliedReward> listPendingRewards() {
        Map<String, Object > params = new HashMap<String, Object>();
        params.put("pending", true);
        try {
            Collection pendingRewardDocs = appliedRewardRepository.find(params);
            ArrayList<AppliedReward> appliedRewards = new ArrayList<AppliedReward>();
            for (Object d : pendingRewardDocs) {
                appliedRewards.add(convertAppliedReward((Document) d));
            }
            return appliedRewards;
        } finally {
            appliedRewardRepository.close();
        }
    }

    public AppliedReward findAppliedReward(String id) {
        try {
            Document appliedRewardDoc = (Document)appliedRewardRepository.findOne(id);
            AppliedReward appliedReward = convertAppliedReward(appliedRewardDoc);
            return appliedReward;
        } finally {
            appliedRewardRepository.close();
        }
    }

    public void deletePendingReward(String id) {
        try {
            appliedRewardRepository.deleteOne(id);
        } finally {
            appliedRewardRepository.close();
        }
    }
}
