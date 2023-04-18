--[[
Utility class for fading from one sound to another.
The fade can be cancelled.

This version has the commented code removed so it can be used as a reference (for the Kotlin AudioFader class I'm making)

By udev (@UTheDev)
]]--

local RunService = game:GetService("RunService")
local TweenService = game:GetService("TweenService")

local RepModules = game:GetService("ReplicatedStorage"):WaitForChild("Modules")
local UtilRepModules = RepModules:WaitForChild("Utils")

local Object = require(UtilRepModules:WaitForChild("Object"))
local Signal = require(UtilRepModules:WaitForChild("Signal"))
local Util = require(UtilRepModules:WaitForChild("Utility"))
local TweenGroup = require(UtilRepModules:WaitForChild("TweenGroup"))

local SoundFader = {}
SoundFader.__index = SoundFader
SoundFader.ClassName = script.Name

local function AssertNumber(Obj, ArgNum)
	assert(typeof(Obj) == "number", "Argument", ArgNum, "must be a number")
end

local function LerpValue(a, b, t)
	return a + (b - a) * t
end

--[[
Lerps the volume between two sounds to a destination volume.

Params:
FadeParams <table> - A table of fading parameters which include:
	OldSound <Sound> - The sound instance to fade from.
	NewSound <Sound> - The sound instance to fade to.
	OldSoundVolume <number> - The volume to fade the old sound away from.
	NewSoundVolume <number> - The volume to fade the new sound to.
	Time <number> - The percentage of the fade (from 0-1).
]]--
function SoundFader.Lerp(FadeParams)
	local Time = math.clamp(FadeParams.Time, 0, 1)
	local OldSound = FadeParams.OldSound
	local NewSound = FadeParams.NewSound
	
	if OldSound ~= nil then
		OldSound.Volume = LerpValue(FadeParams.OldSoundVolume, 0, Time)
	end
	if NewSound ~= nil then
		NewSound.Volume = LerpValue(0, FadeParams.NewSoundVolume, Time)
	end
end

function SoundFader.New()
	local Fader = Object.New(SoundFader.ClassName)
	
	--[[
	Store fading in a queue in case another sound is
	queued for transition while a fade is in progress.
	
	If the fade is requested to be reversed, the sound
	that fades back in will be at the end of the list.
	]]--
	
	-- make sure to fix the music fade out bug with this
	local FadeOutTweens = TweenGroup.New()
	
	local NewSoundTween
	
	--[[
	<boolean>: Whether or not the fade transition
			   can be cancelled.
	]]--
	Fader.CanReverse = true
	
	--[[
	<number>: A number of reference to the original volume of the sound
			  that is fading out.
	]]--
	Fader.OldSoundVolume = 1
	
	--[[
	<number>: How long the total fade transition time is.
	]]--
	Fader.SetProperty("TransitionTime", 1, function(Time)
		Fader.TweeningInfo = TweenInfo.new(Time, Enum.EasingStyle.Linear, Enum.EasingDirection.Out)
	end)
	
	--[[
	<Sound>: The current sound instance that is faded in.
	]]--
	Fader.CurrentSound = nil
	
	local function StopNewSoundTween()
		if NewSoundTween ~= nil then
			NewSoundTween:Pause()
			NewSoundTween:Destroy()
			NewSoundTween = nil
		end
	end
	
	--[[
	Stops the current fading transition immediately.
	]]--
	function Fader.Stop()
		StopNewSoundTween()
		FadeOutTweens.KillAll()
	end
	
	--[[
	Fades to the provided sound.
	
	Params:
	Sound <Sound> - The sound instance to fade to.
	]]--
	function Fader.Switch(Sound: Sound?, TargetVolume: number)		
		-- If the new sound is being faded out,
		-- stop that tween
		if Sound then
			FadeOutTweens.Kill(Sound)
		end
		
		StopNewSoundTween()
		local TweeningInfo = Fader.TweeningInfo
		
		-- Fade out the old
		local OldSound = Fader.CurrentSound
		if OldSound then
			FadeOutTweens.Play(OldSound, TweeningInfo, {Volume = 0}, OldSound, function()
				Fader.SoundFadedOut.Fire(OldSound)
			end)
		end
		
		-- Fade in the new
		Fader.CurrentSound = Sound
		Fader.SoundChanged.Fire(Sound)
		
		if Sound then
			NewSoundTween = TweenService:Create(Sound, TweeningInfo, {Volume = TargetVolume})
			NewSoundTween:Play()
		end
	end
	
	--[[
	Fires when a sound has been faded out
	
	Params:
	OldSounds <Sound> - The sound that was faded out.
	]]--
	Fader.SoundFadedOut = Signal.New()
	
	--[[
	Fires when the primary sound instance has changed.
	
	Params:
	NewSound <Sound> - The new primary sound instance
	]]--
	Fader.SoundChanged = Signal.New()
	
	Fader.OnDisposal = function()
		Fader.Stop()
		
		Fader.SoundFadedOut.DisconnectAll()
		Fader.SoundChanged.DisconnectAll()
	end
	
	return Fader
end

return SoundFader