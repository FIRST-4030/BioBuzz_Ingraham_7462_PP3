package org.firstinspires.ftc.teamcode.OpModes;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.ControlHub;
import org.firstinspires.ftc.teamcode.UtilOpModes.Poses;


@Autonomous
public class PP3Auto extends OpMode {
    ControlHub controlHub;
    private Follower follower;
    //bank of poses
    Poses poses = new Poses();
    private Path startToHive(){
        return line(
                poses.autoStartPos,
                poses.shootPos
        ).linear(
                poses.autoStartPos,
                poses.shootPos
        );
    }
    private Path hiveToGarden(){
        return line(
                poses.shootPos,
                //poses.collectConPose,
                poses.collectPos
        ).linear(
                poses.shootPos,
                poses.collectPos
        );
    }
    private Path gardenToHive(){
        return line(
                poses.collectPos,
                poses.shootPos
        ).linear(
                poses.collectPos,
                poses.shootPos
        );
    }
    private Path hiveToPark(){
        return curve(
                poses.shootPos,
                poses.parkContPose,
                poses.parkPos
        ).linear(
                poses.shootPos,
                poses.parkContPose
        );
    }
    private Command autoRoutine(){
        return sequential(
                follow(follower, startToHive()),
                follow(follower, hiveToGarden()),
                follow(follower, gardenToHive()),
                follow(follower, hiveToPark())
        );
    }
    @Override
    public void init() {
        controlHub = new ControlHub(hardwareMap);

        //Reset movements
        Scheduler.reset();
        //initializes follower
//        follower = Constants.create(hardwareMap);
        follower = controlHub.getRobotSpecificPedroConstants();

        //sets pose to "start"
        follower.setPose(poses.autoStartPos);
        follower.update();
    }
    @Override
    public void start() {
        schedule(autoRoutine());
    }

    @Override
    public void loop() {
        Scheduler.execute();
        follower.update();
    }
}
