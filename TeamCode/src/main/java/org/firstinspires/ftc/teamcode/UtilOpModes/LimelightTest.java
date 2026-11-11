package org.firstinspires.ftc.teamcode.UtilOpModes;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.lazy;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.deadline;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.BiobuzzShooter;
import org.firstinspires.ftc.teamcode.Collector;
import org.firstinspires.ftc.teamcode.ControlHub;
import org.firstinspires.ftc.teamcode.Limelight;
import org.firstinspires.ftc.teamcode.Limelight2;
import org.firstinspires.ftc.teamcode.Poses;

import java.util.function.BooleanSupplier;

@Autonomous(name = "Limelight test")
public class LimelightTest extends OpMode {
    //Components and stuff
    ControlHub controlHub;
    private Follower follower;
    private Collector collector;
    private Limelight2 limelight;
    private BiobuzzShooter shooter;
    private Limelight l;

    //bank of poses
    Poses poses = new Poses();

    //Path methods
    private Path startToShoot() {
        return line(poses.startPosLeft, poses.shootPosLeft).linear(poses.startPosLeft, poses.shootPosLeft);
    }

    private Path collectToShoot() {
        return line(poses.collectPosLeft, poses.shootPosLeft).linear(poses.collectPosLeft, poses.shootPosLeft);
    }

    private Path shootToPreCollect() {
        return line(poses.shootPosLeft, poses.preCollectPoseLeft).facingPoint(poses.collectPosLeft);
    }

    private Path preCollectToCollect() {
        return line(poses.preCollectPoseLeft, poses.collectPosLeft).facingPoint(poses.collectPosLeft);
    }

    private Path robotToPark() {
        Pose currentPose = follower.pose();
        return curve(currentPose, poses.of(24, 24), poses.parkPosLeft).linear(currentPose, poses.parkPosLeft);
    }


    /**
     * Set stuff up
     **/
    @Override
    public void init() {
        //initialize components
        controlHub = new ControlHub(hardwareMap);
        collector = new Collector(hardwareMap);
        limelight = new Limelight2(hardwareMap);
        l = new Limelight(hardwareMap);
        shooter = new BiobuzzShooter(hardwareMap, l);

        //april tag pipeline
        limelight.setPipeline(2);

        //Reset movements
        Scheduler.reset();
        follower = controlHub.getRobotSpecificPedroConstants();

        //sets pose to "start"
        follower.setPose(poses.startPosLeft);
        follower.update();
    }

    /**
     * Runs the routine
     **/
    @Override
    public void start() {
        schedule(autoRoutine());
    }

    /**
     * Runs every something
     **/
    @Override
    public void loop() {
        shooter.overridePower();
        collector.setCollector();
        limelight.update();
        Scheduler.execute();
        follower.update();
        telemetry.addData("can see hive blue scoring", limelight.canSeeTagWithID(limelight.Blue_ScoringIDs));
        telemetry.addData("can see hive blue audience", limelight.canSeeTagWithID(limelight.Blue_AudienceIDs));
        telemetry.addData("can see hive red scoring", limelight.canSeeTagWithID(limelight.Red_ScoringIDs));
        telemetry.addData("can see hive red audience", limelight.canSeeTagWithID(limelight.Red_AudienceIDs));

    }

    /**
     * Contains the logic for the routine and where it wants to go
     * Important Lambdas:   instant runs a code bite
     * follow schedules a path to follow
     * wait until waits until a bool is met
     *
     * @return returns sequential containing the routine
     **/

    private Command autoLogic() {
        BooleanSupplier isAtVelocity = () -> shooter.atSpeed();
        return sequential(
                instant(() -> {
                    collector.setCollectorSpeed(Collector.CollectorMode.COLLECT);
                    shooter.setShooterServos(false);
                    shooter.resetTargetVelocity();
                    shooter.setBackSpinPower(0.8);
                }),
                follow(follower, startToShoot())
        );
    }


    /**
     * this method contains the logic for parking automatically
     * Important Lambdas:   deadline runs two or more processes in parallel and stops all processes once one finishes
     * lazy doesn't finalize when the code runs instead it finalizes it values when it's ran
     *
     * @return returns a sequential containing logic for the routine
     **/
    private Command autoRoutine() {
        return sequential(
                deadline(
                        waitMs(30000),
                        autoLogic()
                ),
                instant(() -> collector.setCollectorSpeed(Collector.CollectorMode.IDLE)),
                lazy(() -> follow(follower, robotToPark()))
        );
    }
}

