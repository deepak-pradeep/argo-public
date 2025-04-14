locals {
application_log_conf = 
 <<-EOT
    [INPUT]
        Name tail
        Tag application.*
        Exclude_Path /var/log/containers/*.log
        Path /var/log/containers/mypod*.log
        multiline.parser docker, cri
        DB /var/fluent-bit/state/flb_container.db
        Mem_Buf_Limit 50MB
        Skip_Long_Lines On
        Refresh_Interval 10
        Rotate_Wait 30
        storage.type filesystem
        Read_from_Head Off
  
    [INPUT]
        Name tail
        Tag application.*
        Path /var/log/containers/fluent-bit*
        multiline.parser docker, cri
        DB /var/fluent-bit/state/flb_log.db
        Mem_Buf_Limit 5MB
        Skip_Long_Lines On
        Refresh_Interval 10
        Read_from_Head Off
  
    [INPUT]
        Name tail
        Tag application.*
        Path /var/log/containers/cloudwatch-agent*
        multiline.parser docker, cri
        DB /var/fluent-bit/state/flb_cwagent.db
        Mem_Buf_Limit 5MB
        Skip_Long_Lines On
        Refresh_Interval 10
        Read_from_Head Off
  
    [FILTER]
        Name kubernetes
        Match application.*
        Kube_URL https://kubernetes.default.svc:443
        Kube_Tag_Prefix application.var.log.containers.
        Merge_Log On
        Merge_Log_Key log_processed
        K8S-Logging.Parser On
        K8S-Logging.Exclude Off
        Labels Off
        Annotations Off
        Buffer_Size 0
  
    [OUTPUT]
        Name cloudwatch_logs
        Match application.*
        region ${local.region}
        log_group_name /aws/containerinsights/${local.cluster_name}/application
        log_stream_prefix $${HOSTNAME}-
        auto_create_group true
        extra_user_agent container-insights
        workers 1
  EOT
}
