import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  srcDir: "pages",
  base: "/Krafter/",

  title: "Krafter",
  description: "Content-rich bot software",
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      {text: 'Home', link: '/'},
      {text: 'Usage', link: '/users/getting-started'},
      {text: 'API', link: '/api/getting-started'},
      {text: 'Config', link: '/config'}
    ],

    logo: '/Krafter_Trans.png',

    search: {
      provider: 'local'
    },

    editLink: {
      pattern: 'https://github.com/Tywrap-Studios/Krafter/edit/master/docs/pages/:path'
    },

    externalLinkIcon: true,

    sidebar: {
      'users/': [{
        text: 'Usage',
        items: [
          {text: 'Getting Started', link: '/users/getting-started'},
          {text: 'Modules', items: [
              {text: 'AMA', link: '/users/modules/ext-ama'},
              {text: 'Embed Channels', link: '/users/modules/ext-embed-channels'},
              {text: 'Fun and Utility', items: [
                  {text: 'Funtility', link: 'users/modules/funtility/funtility'},
                  {text: 'Utility', link: 'users/modules/funtility/utility'},
                ]
              },
              {text: 'Log Parser', link: '/users/modules/ext-log-parser'},
              {text: 'Minecraft', link: '/users/modules/ext-minecraft'},
              {text: 'Safety & Abuse', link: '/users/modules/ext-safety-and-abuse'},
              {text: 'Suggestions', link: '/users/modules/ext-suggestions'},
              {text: 'Tags', link: '/users/modules/ext-tags'},
            ],
            collapsed: false
          },
          {text: 'PluralKit', link: '/users/pluralkit'},
          {text: 'Finding the files', link: 'config/where-are-they'},
        ]
      }],
      'config/': [{
        text: 'Configuration',
        items: [
          {text: 'Introduction', link: '/config/'},
          {text: 'Where are the config files?', link: '/config/where-are-they'},
        ]
      }]
    },

    socialLinks: [
      {icon: 'github', link: 'https://github.com/Tywrap-Studios/Krafter'},
      {icon: 'youtube', link: 'https://www.youtube.com/channel/UCjdRI_nlvxTw4W2Ldfsf5EA'},
      {icon: 'discord', link: 'https://tiazzz.me/discord'},
    ],

    footer: {
      copyright: '<a href="https://docs.tiazzz.me/Krafter">Krafter Documentation</a> © 2025 by <a href="https://tywrap-studios.tiazzz.me">Tywrap Studios</a> is licensed under <a href="https://creativecommons.org/licenses/by-sa/4.0/">CC BY-SA 4.0</a><img src="https://mirrors.creativecommons.org/presskit/icons/cc.svg" alt="" style="max-width: 1em;max-height:1em;margin-left: .2em;"><img src="https://mirrors.creativecommons.org/presskit/icons/by.svg" alt="" style="max-width: 1em;max-height:1em;margin-left: .2em;"><img src="https://mirrors.creativecommons.org/presskit/icons/sa.svg" alt="" style="max-width: 1em;max-height:1em;margin-left: .2em;">'
    }
  },

  cleanUrls: true,
  lastUpdated: true,
  ignoreDeadLinks: true,
})
