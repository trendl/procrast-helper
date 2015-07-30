app.controller('phCtrl', function phCtrl($scope, $http) {

    $scope.newTaskDifficulty = "0.2";
    $scope.newRewardIntervalUnit = "1";

    $scope.taskListRefresh = function () {
        $http.get("rest/tasks").success(function (response) {
            $scope.taskList = response;
        });
    };

    $scope.newTaskSave = function () {
        var task = { name: $scope.newTaskName, difficulty: $scope.newTaskDifficulty, insertDate: Date.now() };
        $http.post("rest/task", task).success( function () {
            $scope.taskListRefresh();
            $scope.newTaskMessage = "Task saved...";
            $scope.newTaskName = "";
            $scope.newTaskDifficulty = "1";
        });
    };

    $scope.taskComplete = function(id) {
        $http.post("rest/task/" + id + "/complete").success( function (response) {
            $scope.taskListRefresh();
            if (response == "1") {
                $scope.newTaskMessage = "Task completed, check your rewards...";
            } else {
                $scope.newTaskMessage = "Task completed, however there's no applicable reward...";
            }
        });
    };

    $scope.taskDelete = function(id) {
        $http.post("rest/task/" + id + "/delete").success( function() {
            $scope.taskListRefresh();
            $scope.newTaskMessage = "Task deleted...";
        });
    };

    $scope.rewardListRefresh = function () {
        $http.get("rest/rewards").success(function(response) {
            $scope.rewardList = response;
        })
    };

    $scope.newRewardSave = function () {
        var rewardInterval = $scope.newRewardInterval * $scope.newRewardIntervalUnit;
        var reward = { name: $scope.newRewardName, appliedDifficulty: $scope.newRewardAppliedDifficulty, minRepeatInterval: rewardInterval };
        $http.post("rest/reward", reward).success( function () {
            $scope.rewardListRefresh();
            $scope.newRewardMessage = "Message saved...";
            $scope.newRewardName = "";
            $scope.newRewardAppliedDifficulty = "1";
            $scope.newRewardInterval = "";
            $scope.newRewardIntervalUnit = "1";
        });
    };

    $scope.rewardDelete = function (id) {
        $http.post("rest/reward/" + id + "/delete").success( function () {
            $scope.rewardListRefresh();
            $scope.newRewardMessage = "Reward deleted...";
        });
    };

    $scope.pendingRewardListRefresh = function () {
        $http.get("rest/pendingrewards").success( function (response) {
            $scope.rewardItemList = response;
            $scope.pendingRewardList = response;
        });
    };

    $scope.takePendingReward = function (id) {
        $http.post("rest/pendingreward/" + id + "/take").success( function () {
            $scope.pendingRewardListRefresh();
            $scope.pendingRewardsMessage = "Reward taken. Enjoy...";
        })
    };

    $scope.deletePendingReward = function (id) {
        $http.post("rest/pendingreward/" + id + "/delete").success( function () {
            $scope.pendingRewardListRefresh();
            $scope.pendingRewardsMessage = "Reward deleted...";
        });
    };

    $scope.exchangePendingReward = function(id) {
        console.log($scope.exchangeRewardItem[id]);
    };

    var init = function () {
        $scope.taskListRefresh();
        //$scope.rewardListRefresh();
        //$scope.pendingRewardListRefresh();
    };

    init();

});