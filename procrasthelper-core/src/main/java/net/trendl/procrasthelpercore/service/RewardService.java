package net.trendl.procrasthelpercore.service;

import net.homecredit.innovations.mongorepository.CommonMongoRepository;
import net.trendl.procrasthelpercore.domain.AccumulatedCredit;
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

    // this is only possible because we do not have any user management - this app is for one user
    private static final String USER_ID = "1";

    Random rand = new Random(DateTime.now().getMillis());

    @Resource(name = "rewardRepository")
    CommonMongoRepository rewardRepository;

    @Resource(name = "appliedRewardRepository")
    CommonMongoRepository appliedRewardRepository;

    @Resource(name="accumulatedCreditRepository")
    CommonMongoRepository accumulatedCreditRepository;

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
        try {
            Document rewardObject = (Document) rewardRepository.findOne(id);
            Reward r = convert(rewardObject);
            return r;
        } finally {
            rewardRepository.close();
        }
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
        try {
            rewardRepository.save(reward.getId(), reward);
        } finally {
            rewardRepository.close();
        }
    }

    public void save(AppliedReward appliedReward) throws Exception {
        try {
            appliedRewardRepository.save(appliedReward.getId(), appliedReward);
        } finally {
            appliedRewardRepository.close();
        }
    }

    // TODO: refactor this method - too long
    public int applyReward(double difficulty) throws Exception {
        try {
            AppliedReward appliedReward = null;
            AccumulatedCredit accumulatedCredit = new AccumulatedCredit();
            accumulatedCredit.setId(USER_ID);
            accumulatedCredit.setValue(difficulty);
            Map<String, Object> accumulatedCreditMap = (Map<String, Object>) accumulatedCreditRepository.findOne(USER_ID);
            if (accumulatedCreditMap != null) {
                // TODO: fix this conversion, it's awful
                double savedAccumulatedCreditValue = Double.valueOf(String.valueOf(accumulatedCreditMap.get("value")));
                accumulatedCredit.setValue(savedAccumulatedCreditValue + difficulty);
            }

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("appliedDifficulty", Math.floor(accumulatedCredit.getValue()));

            Collection foundRewardDocs = rewardRepository.find(params);
            if (foundRewardDocs.size() > 0) {
                ArrayList<Reward> applicableRewards = new ArrayList<Reward>();
                ArrayList<Reward> rewardsWithinMinimalPeriod = new ArrayList<Reward>();
                for (Object d : foundRewardDocs) {
                    Reward reward = convert((Document) d);
                    // find last applied reward
                    HashMap<String, Object> appliedRewardParams = new HashMap<String, Object>();
                    appliedRewardParams.put("reward.id", reward.getId());
                    Collection appliedRewards = appliedRewardRepository.find(appliedRewardParams, "appliedDate.millis", -1);
                    if (appliedRewards.size() > 0) {
                        AppliedReward lastAppliedReward = convertAppliedReward((Document) appliedRewards.iterator().next());
                        if (lastAppliedReward.getAppliedDate() == null ||
                                lastAppliedReward.getAppliedDate().plus(reward.getMinRepeatInterval() * 60 * 1000).isBeforeNow()) {
                            applicableRewards.add(reward);
                            ;
                        } else {
                            rewardsWithinMinimalPeriod.add(reward);
                        }
                    } else {
                        applicableRewards.add(reward);
                    }
                }
                // select one rewards randomly
                appliedReward = null;
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
                    appliedRewardRepository.save(appliedReward.getId(), appliedReward);
                    accumulatedCredit.setValue(accumulatedCredit.getValue() - appliedReward.getReward().getAppliedDifficulty());
                    accumulatedCreditRepository.save(USER_ID, accumulatedCredit);
                    return 1;
                }
            }

            accumulatedCreditRepository.save(USER_ID, accumulatedCredit);
            return 0;
        } finally {
            rewardRepository.close();
            appliedRewardRepository.close();
            accumulatedCreditRepository.close();
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
