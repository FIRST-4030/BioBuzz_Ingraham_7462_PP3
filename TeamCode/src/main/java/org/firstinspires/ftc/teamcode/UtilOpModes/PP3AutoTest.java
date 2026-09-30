package org.firstinspires.ftc.teamcode.UtilOpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.pedropathing.follower.Follower;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.ivy.Command;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static com.pedropathing.api.Paths.*;


/**
* tuff
 **/
@Autonomous
public class PP3AutoTest extends OpMode {
    private Follower follower;

    //create poseFactory that uses degrees (radians big :( )
    private final PoseFactory poseFactory = PoseFactory.degrees();

    //pose
    private final Pose start = poseFactory.of(9, 9, 0);
    private final Pose park = poseFactory.of(24, 9, 0);
    private final Pose moveIDK = poseFactory.of(135, 9, 180);
    private final Pose controlPose = poseFactory.of(77.5, 77.5, 90);

    //path helper methods
    private Path path1() {
        return curve(start, controlPose, moveIDK).linear(start, moveIDK);
    }
    private Path park() {
        return line(moveIDK, park).linear(moveIDK, park);
    }

    //routines
    private Command testRoutine() {
        return sequential(
                follow(follower, path1()),
                // Add mechanism commands here.
                follow(follower, park())
        );
    }

    @Override
    public void init() {
        //Reset movements
        Scheduler.reset();

        //initializes follower
        follower = Constants.create(hardwareMap);

        //sets pose to "start"
        follower.setPose(start);
        follower.update();
    }

    @Override
    public void start() {
        schedule(testRoutine());
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
    }
}
