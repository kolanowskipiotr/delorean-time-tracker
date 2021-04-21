<#include "/macros/emoji-utils.ftl">

<#macro randomEmoji random altText class = "", text = "">
    <@emojiWithAlt emojis[random.nextInt(549)] altText class text/>
</#macro>
