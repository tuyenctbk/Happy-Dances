package com.example.data

import java.util.Calendar
import java.util.Date

object DailyChallengesCatalog {
    val challenges = listOf(
        DailyDanceChallenge(
            id = "swan_arms",
            title = "Daily Sparkle Move",
            moveName = "Swan Arms & Fluid Ripples",
            description = "Channel your inner graceful swan by undulating your arms like water waves!",
            stepInstructions = listOf(
                "Stand tall with shoulders relaxed and back straight like a castle tower 🏰",
                "Gently raise both arms to the sides at shoulder height 🦢",
                "Lead with your elbows, then wrists, and finally fingertips in a soft wave 🌊",
                "Breathe in as your wings rise, and breathe out as they float down ✨"
            ),
            emoji = "🦢",
            stage = "3-5",
            durationSeconds = 60,
            musicTrack = "swan_lake",
            coachTip = "Feel like you are floating across a peaceful crystal lake! ✨"
        ),
        DailyDanceChallenge(
            id = "butterfly_plie",
            title = "Daily Sparkle Move",
            moveName = "Butterfly Flutter & Plié",
            description = "Combine soft knee bends with fluttering butterfly wings for balance!",
            stepInstructions = listOf(
                "Bring your heels together and toes apart into First Position 🩰",
                "Slowly bend your knees out over your toes into a Demi-Plié 🌸",
                "Flutter your hands like delicate butterfly wings as you lower down 🦋",
                "Straighten your knees gracefully and rise tall to greet the sun ☀️"
            ),
            emoji = "🦋",
            stage = "3-5",
            durationSeconds = 60,
            musicTrack = "chopin_waltz",
            coachTip = "Keep your heels firmly on the ground while your knees bend softly!"
        ),
        DailyDanceChallenge(
            id = "tiptoe_twirl",
            title = "Daily Sparkle Move",
            moveName = "Tiptoe Twirl & Statue Freeze",
            description = "Rise high on tiptoes, spin in a gentle circle, and freeze like a magic statue!",
            stepInstructions = listOf(
                "Rise up onto high tiptoes (Relevé) with your hands rounded overhead 👑",
                "Take tiny quick tiptoe steps to spin in a slow 360-degree circle 🌀",
                "Listen to the musical chime and FREEZE instantly in a graceful pose! 🗿",
                "Hold your balance without wobbling for 5 magical seconds ⭐"
            ),
            emoji = "🌀",
            stage = "6-8",
            durationSeconds = 75,
            musicTrack = "nutcracker_flutes",
            coachTip = "Pick one point on the wall in front of you to look at while spinning!"
        ),
        DailyDanceChallenge(
            id = "royal_curtsey",
            title = "Daily Sparkle Move",
            moveName = "Royal Bow & Grand Curtsey",
            description = "Learn the classical court reverence used to thank the audience and teacher!",
            stepInstructions = listOf(
                "Step your right foot out to the side with an open graceful arm 👑",
                "Slide your left foot behind your right ankle and bend both knees 🩰",
                "Bow your head gently with a radiant smile for your imaginary royal court 🏰",
                "Step up tall and repeat the reverance to the left side! 💖"
            ),
            emoji = "👑",
            stage = "6-8",
            durationSeconds = 60,
            musicTrack = "bach_minuet",
            coachTip = "Always finish with a royal ballerina smile to your audience!"
        ),
        DailyDanceChallenge(
            id = "flamingo_balance",
            title = "Daily Sparkle Move",
            moveName = "Flamingo Passé Balance",
            description = "Hold a strong flamingo balance to build steady ankles and core strength!",
            stepInstructions = listOf(
                "Stand tall on your standing leg like a strong tree trunk 🌳",
                "Draw your working foot up the side of your leg until your toe touches your knee (Passé) 🦩",
                "Bring your arms into a gentle oval in front of your chest (First Port de Bras) 🩰",
                "Hold for 15 seconds, then switch and balance on the other leg! ✨"
            ),
            emoji = "🦩",
            stage = "6-8",
            durationSeconds = 75,
            musicTrack = "moonlight_sonata",
            coachTip = "Squeeze your tummy muscles softly to keep your flamingo steady!"
        ),
        DailyDanceChallenge(
            id = "spring_allegro_hops",
            title = "Daily Sparkle Move",
            moveName = "Spring Allegro Little Jumps",
            description = "Practice cheerful sautés in first position to build springy jumping power!",
            stepInstructions = listOf(
                "Start in first position with hands resting lightly on your hips 🩰",
                "Demi-plié to prepare, then spring straight up into the air! 🚀",
                "Point your toes downward towards the floor while in mid-air ✨",
                "Land softly: toe-ball-heel, sinking into a gentle plié cushion 🌸",
                "Complete 8 springy jumps in rhythm with Vivaldi's Spring melody! 🎵"
            ),
            emoji = "🌻",
            stage = "9-12",
            durationSeconds = 90,
            musicTrack = "spring_allegro",
            coachTip = "Land like a quiet cat—never let your heels thump on the floor!"
        ),
        DailyDanceChallenge(
            id = "rainbow_toe_paint",
            title = "Daily Sparkle Move",
            moveName = "Rainbow Floor Arc Paint",
            description = "Draw massive vibrant circles with your pointed toe (Rond de Jambe)!",
            stepInstructions = listOf(
                "Stand in first position and point your right toe forward to the front (Tendu Devant) 🌈",
                "Trace a giant semicircular arc along the floor all the way to the back (Rond de Jambe à Terre) 🎨",
                "Pass through first position with heels touching and sweep another arc! 🩰",
                "Imagine you are painting a radiant pastel rainbow across the studio floor ✨"
            ),
            emoji = "🌈",
            stage = "3-5",
            durationSeconds = 60,
            musicTrack = "chopin_waltz",
            coachTip = "Keep your standing leg straight and tall while your painting toe glides!"
        ),
        DailyDanceChallenge(
            id = "cosmic_arabesque",
            title = "Daily Sparkle Move",
            moveName = "Cosmic Star Arabesque",
            description = "Extend your leg straight behind like a shooting star traveling the cosmos!",
            stepInstructions = listOf(
                "Reach your front arm forward pointing towards the highest star in the sky 🌟",
                "Slide your back leg straight behind you with pointed toes off the floor 💫",
                "Lengthen your spine and hold the majestic arabesque line without arching backwards 🚀",
                "Gently lower and repeat on the opposite side to balance your powers! 🪐"
            ),
            emoji = "🪐",
            stage = "9-12",
            durationSeconds = 90,
            musicTrack = "moonlight_sonata",
            coachTip = "Reach long through your fingertips and toes, reaching to the stars!"
        )
    )

    fun getTodayChallenge(): DailyDanceChallenge {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val index = dayOfYear % challenges.size
        return challenges[index]
    }

    fun getChallengeById(id: String): DailyDanceChallenge {
        return challenges.find { it.id == id } ?: challenges[0]
    }
}
