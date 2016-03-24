Automatic Builder for Maven Artifact Trees
==========================================
AbMat keeps you on top of cross-library failures in your Java-Maven spagetti pile.

Licence
=======
AbMat is released under the Apache 2.0 licence.

Goals
=====
* Every one of your maven projects can depend on released artifacts (no need to depend on SNAPSHOTs)
* Every change pushed to a maven project (even if not released) should have every downstream depending project built against it, to be sure that once released, it will not break anything. This should be known BEFORE pushing the change to master/trunk

Features
========
* On a dev box, should be able to point this at a local pom.xml, and it would build that pom and everything downstream that exists within the moniored repos
* When a pull-request is created on one of the monitored-repos, this should trigger and post the report into that pull-requests's comments
* When reading a pull-request, look for a special tagline indicating another pull-request that this one depends on. In that case, build those upstream deps first
* When triggering, this should build all tested-projects that contain changes since the mainline branch, and then build any poms in the monitored-repos that depend on the built projects

Steps
=====
1. Check out all monitored-repos somewhere (see below for which branches to use)
2. Find all projects (by their pom.xml files) in the monitored-repos
3. Map all projects together into a DAG (if you find a cycle, explode with lots of complaining)
4. Given a list of required projects (see below on how to get this list), build and install the top one
5. Modify all projects to depend on that SNAPSHOT you just installed
6. Build the next project that depends on only artifacts previously installed
7. Repeat until all projects have been built

Finding the right branches and projects depends on the scenario:

* For a pull request, the branch is the one in the pull request, and the projects are any projects with changes since the branch left mainline
* For a dev machine, the branch is the one checked out currently, and the project is just the one you pointed at
