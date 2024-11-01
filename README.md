# configuration

Profile --> Notification Rules -->  cn notifier

- Add new rule
    - Events related to following projects and build configurations
    - Root Project
- 
- New Project
    - Parameters
        - Add new parameter
            - env.WEBHOOK_URL
            - Environment variable (env.)
            - https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key={key}
            - env.AT_MOBILE 可选
            - env.EXT_PARAM 可选 需要输出的参数，多个参数用,分隔
