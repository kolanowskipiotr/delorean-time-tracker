<#include "/macros/emoji-utils.ftl">

<#macro randomEmoji random altText>
    <@emojiWithAlt emojis[random.nextInt(549)] altText/>
</#macro>
