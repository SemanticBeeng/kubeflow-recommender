local env = std.extVar("__ksonnet/environments");
local params = std.extVar("__ksonnet/params").components["recommender1-service"];

local k = import "k.libsonnet";
local tfservingService = import "kubeflow/tf-serving/tf-serving-service-template.libsonnet";
local util = import "kubeflow/tf-serving/util.libsonnet";

tfservingService.new(env, params).all
