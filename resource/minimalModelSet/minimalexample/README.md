# MinimalExample

Minimal might be a bit misleading. It used to be, though. 

## Setups
* `default.*` and `My.resourceenvironment` is the default setup with a closed workload and a FCFS resource.
* `procsharing.*` allocates the service to a processor sharing resource.
* `openworkload.*` is a usage scenario with an open workload.

## Scalingpolicies
- exist, but are not yet tested

## launchconfigs
* `fcfs` and `procsharing` indicate the type of resource. default is `fcfs`.
* `open` and `closed` indicate the type of usage scenario. default is `closedload`.

## Motivation 
I put multiple setups into one project, because i usually want to run simulation on any combination of open or closed workload and FCFS and processor sharing resource. 
Having one set of model and changing the properties got to confusing. And creating for different projects appeared to much effort as well. 
