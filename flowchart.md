graph TD
    A[Start] --> TR_WF_DISPATCH
    A --> TR_PUSH
    A --> TR_PR
    A --> TR_ISSUE_EDITED
    A --> TR_SCHEDULE

    subgraph Workflow Triggers
        TR_WF_DISPATCH["<strong>Manual Trigger</strong><br>(workflow_dispatch)<br><em>Inputs: release_version, envname, deploy_options</em>"]
        TR_PUSH["<strong>Push Event</strong><br>(branches-ignore: main)"]
        TR_PR["<strong>Pull Request Event</strong><br>(any branch)"]
        TR_ISSUE_EDITED["<strong>Issue Edited Event</strong><br>(issue.types: [edited])"]
        TR_SCHEDULE["<strong>Schedule Event</strong><br>(cron - <em>currently commented out</em>)"]
    end

    subgraph Jobs and Execution Paths
        %% CD Workflow Path
        TR_WF_DISPATCH -- "github.event_name == 'workflow_dispatch'" --> JOB_CD("<strong>Job: cd-workflow</strong>")

        %% CI Features Path
        JOB_CI_FEATURES("<strong>Job: ci-features</strong>")
        TR_PUSH -- "github.event_name == 'push'" --> JOB_CI_FEATURES
        TR_PR -- "github.event_name == 'pull_request'<br>AND github.base_ref != 'main'" --> JOB_CI_FEATURES

        %% Pre-Check Path
        JOB_PRE_CHECK("<strong>Job: pre-check</strong")
        TR_PR -- "github.event_name == 'pull_request'<br>AND github.base_ref == 'main'<br>AND github.event.pull_request.merged != true" --> JOB_PRE_CHECK

        %% CI on Merge to Main Path
        JOB_CI_MERGE("<strong>Job: ci-on-merge-to-main</strong>")
        TR_PR -- "github.event_name == 'pull_request'<br>AND github.event.pull_request.merged == true<br>AND github.event.pull_request.base.ref == 'main'" --> JOB_CI_MERGE

        %% Open Source Gov Path
        JOB_OS_GOV("<strong>Job: opensource-gov</strong>")
        TR_ISSUE_EDITED -- "github.event_name == 'issues'<br>AND github.event.issue.state == 'open'" --> JOB_OS_GOV

        %% Stale Path
        JOB_STALE("<strong>Job: stale</strong>")
        TR_SCHEDULE -- "github.event_name == 'schedule'" --> JOB_STALE
    end
