<#assign emojis = [
'🌄' ,'🙆' ,'🙇' ,'🙋' ,'🙌' ,'🙍' ,'🙎' ,'🙏' ,'✂' ,'✈' ,'✉' ,'✊' ,'✋' ,'✌' ,'✏' ,'❄' ,'❤' ,'🚀' ,'🚃' ,
'🚄' ,'🚅' ,'🚇' ,'🚉' ,'🚌' ,'🚏' ,'🚑' ,'🚒' ,'🚓' ,'🚕' ,'🚗' ,'🚙' ,'🚚' ,'🚢' ,'🚤' ,'🚥' ,'🚧' ,'🚨' ,'🚩' ,
'🚪' ,'🚫' ,'🚬' ,'🚲' ,'🚶' ,'🚽' ,'🛀' ,'⌚' ,'⌛' ,'⏰' ,'⏳' ,'☁' ,'☎' ,'☔' ,'☕' ,'♨' ,'♻' ,'♿' ,'⚓' ,
'⚡' ,'⚽' ,'⚾' ,'⛄' ,'⛅' ,'⛪' ,'⛲' ,'⛳' ,'⛵' ,'⛺' ,'⭐' ,'⛽' ,'🃏' ,'🌀' ,'🌁' ,'🌂' ,'🌃' ,'🌄' ,'🌅' ,
'🌆' ,'🌇' ,'🌈' ,'🌉' ,'🌊' ,'🌋' ,'🌏' ,'🌙' ,'🌛' ,'🌟' ,'🌠' ,'🌰' ,'🌱' ,'🌴' ,'🌵' ,'🌷' ,'🌸' ,'🌹' ,'🌺' ,
'🌻' ,'🌼' ,'🌽' ,'🌾' ,'🌿' ,'🍀' ,'🍁' ,'🍂' ,'🍃' ,'🍄' ,'🍅' ,'🍆' ,'🍇' ,'🍈' ,'🍉' ,'🍊' ,'🍌' ,'🍍' ,'🍎' ,
'🍏' ,'🍑' ,'🍒' ,'🍓' ,'🍔' ,'🍕' ,'🍖' ,'🍗' ,'🍘' ,'🍙' ,'🍚' ,'🍛' ,'🍜' ,'🍝' ,'🍞' ,'🍟' ,'🍠' ,'🍡' ,'🍢' ,
'🍣' ,'🍤' ,'🍥' ,'🍦' ,'🍧' ,'🍨' ,'🍩' ,'🍪' ,'🍫' ,'🍬' ,'🍭' ,'🍮' ,'🍯' ,'🍰' ,'🍱' ,'🍲' ,'🍳' ,'🍴' ,'🍵' ,
'🍶' ,'🍷' ,'🍸' ,'🍹' ,'🍺' ,'🍻' ,'🎀' ,'🎁' ,'🎂' ,'🎃' ,'🎄' ,'🎅' ,'🎆' ,'🎇' ,'🎈' ,'🎉' ,'🎊' ,'🎋' ,'🎌' ,
'🎍' ,'🎎' ,'🎏' ,'🎐' ,'🎑' ,'🎒' ,'🎓' ,'🎠' ,'🎡' ,'🎢' ,'🎣' ,'🎤' ,'🎥' ,'🎦' ,'🎧' ,'🎨' ,'🎩' ,'🎪' ,'🎫' ,
'🎬' ,'🎭' ,'🎮' ,'🎯' ,'🎰' ,'🎱' ,'🎲' ,'🎳' ,'🎴' ,'🎵' ,'🎶' ,'🎷' ,'🎸' ,'🎹' ,'🎺' ,'🎻' ,'🎽' ,'🎾' ,'🎿' ,
'🏀' ,'🏁' ,'🏂' ,'🏃' ,'🏄' ,'🏆' ,'🏈' ,'🏊' ,'🏠' ,'🏡' ,'🏢' ,'🏣' ,'🏥' ,'🏦' ,'🏧' ,'🏨' ,'🏩' ,'🏪' ,'🏫' ,
'🏬' ,'🏭' ,'🏮' ,'🏯' ,'🏰' ,'🐌' ,'🐍' ,'🐎' ,'🐑' ,'🐒' ,'🐔' ,'🐗' ,'🐘' ,'🐙' ,'🐚' ,'🐛' ,'🐜' ,'🐝' ,'🐞' ,
'🐟' ,'🐠' ,'🐡' ,'🐢' ,'🐣' ,'🐤' ,'🐥' ,'🐦' ,'🐧' ,'🐨' ,'🐩' ,'🐫' ,'🐬' ,'🐭' ,'🐮' ,'🐯' ,'🐰' ,'🐱' ,'🐲' ,
'🐳' ,'🐴' ,'🐵' ,'🐶' ,'🐷' ,'🐸' ,'🐹' ,'🐺' ,'🐻' ,'🐼' ,'🐽' ,'🐾' ,'👀' ,'👂' ,'👃' ,'👄' ,'👅' ,'👆' ,'👇' ,
'👈' ,'👉' ,'👊' ,'👋' ,'👌' ,'👍' ,'👎' ,'👏' ,'👐' ,'👑' ,'👒' ,'👓' ,'👔' ,'👕' ,'👖' ,'👗' ,'👘' ,'👙' ,'👚' ,
'👛' ,'👜' ,'👝' ,'👞' ,'👟' ,'👠' ,'👡' ,'👢' ,'👣' ,'👤' ,'👦' ,'👧' ,'👨' ,'👩' ,'👪' ,'👫' ,'👮' ,'👯' ,'👰' ,
'👱' ,'👴' ,'👶' ,'👷' ,'👸' ,'👹' ,'👺' ,'👻' ,'👼' ,'👽' ,'👾' ,'👿' ,'💀' ,'💁' ,'💂' ,'💃' ,'💄' ,'💅' ,'💆' ,
'💇' ,'💈' ,'💉' ,'💊' ,'💋' ,'💌' ,'💍' ,'💎' ,'💏' ,'💐' ,'💑' ,'💒' ,'💓' ,'💔' ,'💕' ,'💖' ,'💗' ,'💘' ,'💙' ,
'💚' ,'💛' ,'💜' ,'💝' ,'💞' ,'💟' ,'💠' ,'💡' ,'💢' ,'💣' ,'💤' ,'💥' ,'💦' ,'💧' ,'💨' ,'💩' ,'💪' ,'💫' ,'💬' ,
'💮' ,'💯' ,'💰' ,'💲' ,'💳' ,'💵' ,'💸' ,'💺' ,'💻' ,'💼' ,'💽' ,'💾' ,'💿' ,'📀' ,'📃' ,'📅' ,'📆' ,'📈' ,'📉' ,
'📌' ,'📍' ,'📎' ,'📓' ,'📔' ,'📕' ,'📖' ,'📞' ,'📟' ,'📠' ,'📡' ,'📣' ,'📦' ,'📧' ,'📫' ,'📰' ,'📱' ,'📷' ,'📹' ,
'📺' ,'📻' ,'📼' ,'🔊' ,'🔋' ,'🔌' ,'🔎' ,'🔐' ,'🔑' ,'🔒' ,'🔓' ,'🔔' ,'🔜' ,'🔥' ,'🔦' ,'🔧' ,'🔨' ,'🔩' ,'🔪' ,
'🔫' ,'🔮' ,'🗻' ,'🗼' ,'🗽' ,'🗾' ,'🗿' ,'😴' ,'🚁' ,'🚂' ,'🚆' ,'🚈' ,'🚊' ,'🚍' ,'🚎' ,'🚐' ,'🚔' ,'🚖' ,'🚘' ,
'🚛' ,'🚜' ,'🚝' ,'🚞' ,'🚟' ,'🚠' ,'🚡' ,'🚣' ,'🚦' ,'🚮' ,'🚵' ,'🚿' ,'🛁' ,'🌍' ,'🌎' ,'🌜' ,'🌝' ,'🌞' ,'🌲' ,
'🌳' ,'🍋' ,'🍐' ,'🍼' ,'🏇' ,'🏉' ,'🏤' ,'🐀' ,'🐁' ,'🐂' ,'🐃' ,'🐄' ,'🐅' ,'🐆' ,'🐇' ,'🐈' ,'🐉' ,'🐊' ,'🐋' ,
'🐏' ,'🐐' ,'🐓' ,'🐕' ,'🐖' ,'🐪' ,'👬' ,'👭' ,'📬' ,'📭' ,'📯' ,'🔬' ,'🔭','⛰️', '🗻', '🏞️', '🌲', '🌳', '🌵',
'☀️', '🏖️', '🏝️', '🐬', '🦇', '🐸', '🕷️', '🦑', '🐠', '🐡', '🐟','🐙', '🦈', '🐚','🐳', '🌊', '☁️', '🌙', '🍋', '⛅',
'🌞', '🌻', '🌤️', '🌇', '🏝️', '🐦', '🌲', '🌴', '⛲', '⛱️', '🏕️', '🏔️', '🌅', '🙅', '🌋', '🌄']>

<#macro randomEmoji random altText>
    <a title="${altText?html}">
        ${emojis[random.nextInt(549)]}
    </a>
</#macro>
